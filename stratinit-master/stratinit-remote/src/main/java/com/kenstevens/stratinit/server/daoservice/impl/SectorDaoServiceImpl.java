package com.kenstevens.stratinit.server.daoservice.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.model.AttackType;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.CityCapturedBattleLog;
import com.kenstevens.stratinit.model.CityMove;
import com.kenstevens.stratinit.model.CityNukedBattleLog;
import com.kenstevens.stratinit.model.CityPK;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.LaunchedSatellite;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitAttackedBattleLog;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.server.daoservice.CityBuilderService;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;
import com.kenstevens.stratinit.server.daoservice.LogDaoService;
import com.kenstevens.stratinit.server.daoservice.MessageDaoService;
import com.kenstevens.stratinit.server.daoservice.MoveSeen;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.remote.event.EventQueue;
import com.kenstevens.stratinit.supply.Supply;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;

// TODO OPT pull stuff out of daoservices that only hit cache
@Service
public class SectorDaoServiceImpl implements SectorDaoService {
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private GameDao gameDao;
	@Autowired
	private SectorDao sectorDao;
	@Autowired
	private UnitDao unitDao;
	@Autowired
	private UnitDaoService unitDaoService;
	@Autowired
	private GameDaoService gameDaoService;
	@Autowired
	private LogDaoService logDaoService;
	@Autowired
	private MessageDaoService messageDaoService;
	@Autowired
	private EventQueue eventQueue;
	@Autowired
	private CityBuilderService cityBuilderService;
	@Autowired
	private DataCache dataCache;

	public void survey(Nation nation) {
		List<City> cities = sectorDao.getCities(nation);
		List<Unit> units = unitDao.getUnits(nation);
		Set<Sector> seen = new HashSet<Sector>();
		seen.addAll(seeFromCities(cities));
		seen.addAll(seeFromUnits(units));
		MoveSeen moveSeen = new MoveSeen(nation, this, unitDaoService);
		// first ignore subs
		addSectorsSeen(null, seen, moveSeen);
		// now see subs
		for (Unit unit : units) {
			if (unit.isCanSeeSubs()) {
				addSectorsSeen(unit, seen, moveSeen);
			}
		}
		moveSeen.persist();
	}

	private Set<Sector> seeFromCities(List<City> cities) {
		Set<Sector> cseen = new HashSet<Sector>();
		for (City city : cities) {
			seeFromCity(cseen, city);
		}
		return cseen;
	}

	private void seeFromCity(Set<Sector> cseen, City city) {
		int radius = city.getSightRadius();
		cseen.addAll(dataCache.getWorld(city.getGameId()).getSectorsWithin(
				city.getCoords(), radius));
	}

	private Set<Sector> seeFromUnits(List<Unit> units) {
		Set<Sector> unitSectors = new HashSet<Sector>();
		for (Unit unit : units) {
			unitSectors.addAll(getSectorsSeen(unit));
		}
		return unitSectors;
	}

	private void addSectorsSeen(Unit unitSeeing, Collection<Sector> seen,
			MoveSeen moveSeen) {
		for (Sector sector : seen) {
			addSectorSeen(unitSeeing, sector, moveSeen);
		}
	}

	private void addSectorSeen(Unit unitSeeing, Sector sector, MoveSeen moveSeen) {
		moveSeen.add(unitSeeing, sector);
	}

	public void buildUnit(CityPK cityPK, Date buildTime) {
		City city = sectorDao.findCity(cityPK);
		cityBuilderService.buildUnit(city, buildTime);
	}

	// TODO DEPL persistence.xml read-only on getters
	public WorldView getAllWorldView(Nation nation) {
		Game game = gameDaoService.findGame(nation.getGame().getId());
		List<Sector> sectors;
		sectors = dataCache.getSectors(nation.getGameId());
		return getWorldView(nation, sectors, unitDao.getUnits(game));
	}

	public WorldView getSupplyWorldView(Unit unit) {
		return getRadiusWorldView(unit, Constants.SUPPLY_RADIUS);
	}

	private WorldView getRadiusWorldView(Unit unit, int distance) {
		return getRadiusWorldView(unit.getCoords(), unit.getNation(), distance);
	}

	private WorldView getRadiusWorldView(SectorCoords coords, Nation nation,
			int distance) {
		List<Sector> sectors = dataCache.getWorld(nation.getGameId())
				.getSectorsWithin(coords, distance);
		return getWorldView(nation, sectors, unitDaoService.getTeamUnits(
				nation, gameDao.getAllies(nation)));
	}

	public WorldView getInterdictionWorldView(Unit unit, Nation nation) {
		// Need to get a big enough world view to see all surrounding units and
		// their supply
		return getRadiusWorldView(unit.getCoords(), nation,
				Constants.SUPPLY_RADIUS + UnitBase.largestInterdictsSight);
	}

	public WorldView getInterceptionWorldView(SectorCoords coords, Nation nation) {
		// Need to get a big enough world view to see all surrounding units and
		// their supply
		return getRadiusWorldView(coords, nation, Constants.INTERCEPTION_RADIUS);
	}

	public WorldView getSeenWorldView(Nation nation) {
		Collection<Sector> sectors = sectorDao
				.getNationSectorsSeenSectors(nation);
		Set<Unit> units = unitDaoService.getTeamUnits(nation, gameDao
				.getAllies(nation));
		return getWorldView(nation, sectors, units);
	}

	private WorldView getWorldView(Nation nation, Collection<Sector> sectors,
			Collection<Unit> units) {
		WorldView worldView = new WorldView(nation, gameDao
				.getMyRelationsAsMap(nation), gameDao
				.getTheirRelationTypesAsMap(nation));
		setWorldSectorsFromCities(sectors, worldView);
		if (units != null) {
			setWorldSectorsFromUnits(nation, worldView, units);
		}
		return worldView;
	}

	private void setWorldSectorsFromCities(Collection<Sector> sectors,
			WorldView worldView) {
		List<WorldSector> friendlyCities = new ArrayList<WorldSector>();
		Map<SectorCoords, City> cityMap = sectorDao.getCityMap(worldView
				.getGame());
		for (Sector sector : sectors) {
			WorldSector worldSector = new WorldSector(sector);
			worldView.setWorldSector(worldSector);
			if (!worldSector.isPlayerCity()) {
				continue;
			}
			City city = cityMap.get(sector.getCoords());
			if (city != null) {
				Nation cityNation = city.getNation();
				worldSector.setNation(cityNation);
				setWorldSectorRelations(worldView, worldSector, cityNation);
				worldSector.setCityType(city.getType());
				if (worldSector.onMyTeam()) {
					friendlyCities.add(worldSector);
				}
			}
		}
		for (WorldSector citySector : friendlyCities) {
			citySector.setSuppliesLand(true);
			if (citySector.isPort()) {
				citySector.setSuppliesNavy(true);
			}
		}
	}

	private void setWorldSectorsFromUnits(Nation nation, WorldView worldView,
			Collection<Unit> units) {
		for (Unit unit : units) {
			WorldSector worldSector = worldView.getWorldSector(unit.getCoords());
			if (worldSector == null) {
				continue;
			}
			Nation unitNation = unit.getNation();
			if (!worldSector.isPlayerCity()) {
				worldSector.setNation(unitNation);
				setWorldSectorRelations(worldView, worldSector, unitNation);
			}
			worldSector.addFlak(unit.getFlak());
			worldSector.pickTopUnit(unit);

			if (unitNation.equals(nation)) {
				setSupply(unit, worldSector);
			}
			if (worldSector.onMyTeam()) {
				setCarrier(unit, worldSector);
			}
		}
	}

	private void setCarrier(Unit unit, WorldSector worldSector) {
		if (unit.getType().equals(UnitType.CARRIER)) {
			worldSector.setHoldsFriendlyCarrier(true);
		}
		if (unit.isCapital()) {
			worldSector.setHoldsMyCapital(true);
		}
	}

	private void setSupply(Unit unit, WorldSector worldSector) {
		if (worldSector.isWater() && unit.isTransport()) {
			worldSector.setHoldsMyTransport(true);
		}
		if (unit.getAmmo() > 0) {
			worldSector.setSuppliesLand(worldSector.isSuppliesLand()
					|| unit.isSuppliesLand());
			worldSector.setSuppliesNavy(worldSector.isSuppliesNavy()
					|| unit.isSuppliesNavy());
		}
	}


	private void setWorldSectorRelations(WorldView worldView,
			WorldSector worldSector, Nation sectorNation) {
		RelationType myRelation = worldView.getMyRelation(sectorNation);
		// Pick the worst relation
		if (worldSector.getMyRelation() == null
				|| worldSector.getMyRelation().compareTo(myRelation) > 0) {
			worldSector.setMyRelation(myRelation);
		}
		RelationType theirRelation = worldView.getTheirRelation(sectorNation);
		if (worldSector.getTheirRelation() == null
				|| worldSector.getTheirRelation().compareTo(theirRelation) > 0) {
			worldSector.setTheirRelation(theirRelation);
		}
	}

	public int getLandUnitWeight(WorldSector worldSector) {
		Collection<Unit> units = unitDao.getUnits(worldSector);
		int retval = 0;
		for (Unit unit : units) {
			if (!unit.isLand()) {
				continue;
			}
			retval += unit.getWeight();
		}
		return retval;
	}

	public List<Sector> getSectorsSeen(Unit unit) {
		return dataCache.getWorld(unit.getGameId()).getSectorsWithin(
				unit.getCoords(), unit.getSight());
	}

	public void merge(City city) {
		sectorDao.merge(city);
	}

	// Returns old owner
	public Nation captureCity(Nation nation, Sector sector) {
		City city;
		Nation opponent = null;
		city = sectorDao.getCity(sector);
		if (city == null) {
			city = new City(sector, nation, UnitType.BASE);
			sectorDao.persist(city);
		} else {
			opponent = city.getNation();
			cityBuilderService.cityCapturedBuildChange(city);
			sectorDao.transferCity(city, nation);
		}
		Sector citySector = dataCache.getWorld(nation.getGameId()).getSector(
				sector.getCoords());
		citySector.setType(SectorType.PLAYER_CITY);
		sectorDao.merge(citySector);
		surveyFromCity(city);
		explodeSupply(nation, sector.getCoords());
		return opponent;
	}

	public void explodeSupply(Nation nation, SectorCoords coords) {
		WorldView worldView = getAllWorldView(nation);
		Collection<Unit> units = unitDao.getUnitsWithin(worldView, nation,
				coords, Constants.SUPPLY_RADIUS);
		for (Unit unit : units) {
			Supply supply = new Supply(worldView);
			if (supply.inSupply(unit)) {
				supply.resupply(unit);
				unitDao.merge(unit);
			}
		}
	}

	public void updateSeen(CoordMeasure coordMeasure, List<Unit> units,
			MoveSeen moveSeen) {
		unitsSee(units, moveSeen);
		unitsSeen(coordMeasure, units, moveSeen, false);
	}

	public void unitsSeen(CoordMeasure coordMeasure, List<Unit> units,
			MoveSeen moveSeen, boolean attacking) {
		Collection<Nation> nations;
		if (units.isEmpty()) {
			return;
		}
		Unit topUnit = units.get(0);
		Nation movingNation = topUnit.getNation();
		if (topUnit.isSubmarine() && !attacking) {
			nations = unitDao.getOtherNationsThatCanSeeThisSub(coordMeasure,
					topUnit);
		} else {
			nations = sectorDao.getOtherNationsThatSeeThisSector(movingNation,
					topUnit.getCoords());
		}
		if (nations.isEmpty()) {
			// TODO OPT move this elsewhere
			nations = unitDao.getOtherNationsThatSeeThisUnit(topUnit);
			for (Nation nation : nations) {
				unitDaoService.disable(nation, units);
			}
		} else {
			if (!attacking) { // might have sunk enemy
				nations = unitDao.getOtherNationsThatCanSeeThisUnit(
						coordMeasure, topUnit);
			}
			for (Nation nation : nations) {
				if (!nation.equals(movingNation)) {
					moveSeen.add(nation, units);
				}
			}
		}
	}

	private void unitsSee(List<Unit> units, MoveSeen moveSeen) {
		for (Unit unit : units) {
			addSectorsSeen(unit, getSectorsSeen(unit), moveSeen);
		}
	}

	public void satelliteSees(LaunchedSatellite satellite, MoveSeen moveSeen) {
		addSectorsSeen(null, dataCache.getWorld(satellite.getGameId())
				.getSectorsWithin(satellite.getCoords(),
						Constants.SATELLITE_SIGHT), moveSeen);
	}

	public WorldSector refreshWorldSector(Nation nation, WorldView worldView,
			WorldSector targetSector) {
		List<Sector> sectors = new ArrayList<Sector>();
		sectors.add(dataCache.getWorld(nation.getGameId()).getSector(
				targetSector.getCoords()));
		setWorldSectorsFromCities(sectors, worldView);
		Collection<Unit> units = unitDao.getUnits(targetSector);
		setWorldSectorsFromUnits(nation, worldView, units);
		return worldView.getWorldSector(targetSector);
	}

	public Nation captureCity(Nation nation, SectorCoords city) {
		Sector sector = dataCache.getWorld(nation.getGameId()).getSector(city);
		return captureCity(nation, sector);
	}

	public Set<Nation> devastate(Unit attackerUnit, Sector sector, boolean isInitialAttack) {
		Set<Nation> retval = Sets.newHashSet();
		if (sector.getType() == SectorType.PLAYER_CITY) {
			City city = sectorDao.getCity(sector);
			if (city == null) {
				throw new IllegalStateException("Cannot find player city at "
						+ sector);
			}
			CityNukedBattleLog cityNukedBattleLog = new CityNukedBattleLog(
					attackerUnit, city.getNation(), sector.getCoords());
			if (isInitialAttack) {
				gameDaoService.setRelation(city.getNation(), attackerUnit
						.getNation(), RelationType.WAR, true);
				retval.add(city.getNation());
			}
			logDaoService.persist(cityNukedBattleLog);
			remove(city);
			sector.setType(SectorType.WASTELAND);
			sectorDao.merge(sector);
		}
		List<Unit> units = Lists.newArrayList(unitDao.getUnits(sector));
		for (Unit unit : units) {
			unitDaoService.killUnit(unit);
			UnitAttackedBattleLog unitAttackedBattleLog = new UnitAttackedBattleLog(
					AttackType.NUKE, attackerUnit, unit, sector.getCoords());
			unitAttackedBattleLog.setDefenderDied(true);
			logDaoService.persist(unitAttackedBattleLog);
		}
		return retval;
	}


	public Result<City> updateCity(Nation nation, SectorCoords coords,
			UpdateCityField field, UnitType build, UnitType nextBuild,
			boolean switchOnTechChange, SectorCoords nextCoords) {

		City city = sectorDao.getCity(nation, coords);

		Result<City> retval;
		if (field == UpdateCityField.BUILD) {
			retval = cityBuilderService.updateBuild(nation, city, build);
		} else if (field == UpdateCityField.NEXT_BUILD) {
			retval = cityBuilderService.updateNextBuild(city, nextBuild);
		} else if (field == UpdateCityField.SWITCH_ON_TECH_CHANGE) {
			retval = new Result<City>(true);
			retval.addMessage("Setting switch on tech change in "
					+ city.getCoords() + " to " + switchOnTechChange);
			city.setSwitchOnTechChange(switchOnTechChange);
		} else if (field == UpdateCityField.NEXT_COORDS) {
			retval = new Result<City>(true);
			if (nextCoords == null) {
				retval.addMessage("Cancelling waypoint of city "
						+ city.getCoords());
				clearCityMove(city);
			} else {
				retval.addMessage("Setting waypoint of city "
						+ city.getCoords() + " to " + nextCoords);
				setCityMove(city, nextCoords);
			}
		} else {
			throw new IllegalStateException(
					"Unknown value for UpdateCityField: " + field);
		}

		merge(city);
		retval.setValue(city);
		return retval;
	}

	private void clearCityMove(City city) {
		sectorDao.clearCityMove(city);
	}

	private void surveyFromCity(City city) {
		Set<Sector> cseen = new HashSet<Sector>();
		seeFromCity(cseen, city);
		MoveSeen moveSeen = new MoveSeen(city.getNation(), this, unitDaoService);
		addSectorsSeen(null, cseen, moveSeen);
		moveSeen.persist();
	}

	public void saveIfNew(Nation nation, Sector sector) {
		sectorDao.saveIfNew(nation, sector);
	}

	public void unitSeen(CoordMeasure coordMeasure, Unit unit,
			MoveSeen moveSeen, boolean attacking) {
		unitsSeen(coordMeasure, makeUnitList(unit), moveSeen, attacking);
	}

	private List<Unit> makeUnitList(Unit unit) {
		List<Unit> units = new ArrayList<Unit>();
		units.add(unit);
		return units;
	}

	public void updateSeen(CoordMeasure coordMeasure, Unit unit,
			MoveSeen moveSeen) {
		updateSeen(coordMeasure, makeUnitList(unit), moveSeen);
	}

	public Result<None> cedeCity(City city, Nation nation) {
		Nation oldOwner = city.getNation();
		sectorDao.transferCity(city, nation);
		survey(nation);
		messageDaoService.notify(nation, oldOwner + " " + city.getCoords()
				+ " ceded", oldOwner + " gave you a city at "
				+ city.getCoords());
		return new Result<None>("City ownership transferred from " + oldOwner
				+ " to " + city.getNation(), true);
	}

	@Override
	public void switchCityBuildsFromTechChange(Nation nation, Date buildTime) {
		List<City> cities = sectorDao.getCities(nation);
		for (City city : cities) {
			if (city.isSwitchOnTechChange()
					&& cityBuilderService.switchCityProductionIfTechPermits(city, buildTime)) {
				sectorDao.merge(city);
			}
		}
	}

	@Override
	public Result<None> establishCity(Unit unit) {
		Nation nation = unit.getNation();
		SectorCoords coords = unit.getCoords();
		Sector sector = sectorDao.getWorld(nation.getGameId()).getSector(coords);
		if (!canEstablishCity(nation, sector)) {
			return new Result<None>("You may not establish a city at "+coords+".  Cities can only be established on land or next to land and may not be adjacent to other player cities.", false);
		}
		City city = new City(sector, nation, UnitType.BASE);
		sectorDao.persist(city);
		sector.setType(SectorType.PLAYER_CITY);
		sectorDao.merge(sector);
		CityCapturedBattleLog battleLog = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit,
				null, unit.getCoords());
		logDaoService.persist(battleLog);
		unit.decreaseMobility(Constants.MOB_COST_TO_CREATE_CITY);
		unitDao.merge(unit);
		return new Result<None>("City established at "+coords+".", true);
	}

	private boolean canEstablishCity(Nation nation, Sector sector) {
		return sectorDao.canEstablishCity(nation, sector);
	}

	@Override
	public Result<None> destroyCity(City city) {
		
		Sector sector = sectorDao.getWorld(city.getGameId()).getSector(city.getCoords());
		sector.setType(SectorType.START_CITY);
		sectorDao.merge(sector);

		remove(city);
		
		return new Result<None>("City destroyed at "+sector.getCoords()+".", true);
	}

	@Override
	public void remove(City city) {
		eventQueue.cancel(city);
		clearCityMove(city);
		sectorDao.remove(city);
	}
	
	@Override
	public void cityChanged(Nation nation, City city) {
		surveyFromCity(city);
		explodeSupply(nation, city.getCoords());
	}

	@Override
	public void merge(Sector sector) {
		sectorDao.merge(sector);
	}
	

	@Override
	public void removeCityMoves(Game game) {
		for (CityMove cityMove : Lists.newArrayList(sectorDao.getCityMoves(game))) {
			sectorDao.remove(cityMove);
		}
	}

	@Override
	public void setCityMove(City city, SectorCoords targetCoords) {
		CityMove cityMove = new CityMove(city, targetCoords);
		city.setCityMove(cityMove);
		sectorDao.persist(cityMove);
		merge(city);
	}

}
