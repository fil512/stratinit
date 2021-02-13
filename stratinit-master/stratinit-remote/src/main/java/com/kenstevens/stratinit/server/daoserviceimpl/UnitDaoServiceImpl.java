package com.kenstevens.stratinit.server.daoserviceimpl;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.MessageDaoService;
import com.kenstevens.stratinit.server.daoservice.MoveSeen;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.event.EventQueue;
import com.kenstevens.stratinit.server.remote.move.UnitCommandFactory;
import com.kenstevens.stratinit.server.remote.move.UnitsMove;
import com.kenstevens.stratinit.server.remote.move.UnitsToMove;
import com.kenstevens.stratinit.supply.Supply;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.util.ContainerUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UnitDaoServiceImpl implements UnitDaoService {
	@Autowired
	private UnitDao unitDao;
	@Autowired
	private SectorDao sectorDao;
	@Autowired
	private SectorDaoService sectorDaoService;
	@Autowired
	private MessageDaoService messageDaoService;
	@Autowired
	private EventQueue eventQueue;
	@Autowired
	private DataCache dataCache;
	@Autowired
	private UnitCommandFactory unitCommandFactory;

	public void updateUnit(Unit unit, Date buildTime) {
		Sector sector = dataCache.getWorld(unit.getGameId()).getSector(
				unit.getCoords());
		if (sector.isPlayerCity()) {
			unit.healPercent(Constants.CITY_HEAL_PERCENT);
		} else {
			WorldView worldView = sectorDaoService.getSupplyWorldView(unit);
			Supply supply = new Supply(worldView);
			if (supply.inSupply(unit)) {
				unit.healPercent(Constants.SUPPLY_HEAL_PERCENT);
			}
		}
		unit.addMobility();
		unit.setLastUpdated(buildTime);
		UnitMove unitMove = unit.getUnitMove();
		if (unitMove == null) {
			// TODO REF add convenience method
			City city = sectorDao.findCity(new CityPK(unit.getGame(), unit.getCoords()));
			if (city != null && unit.getNation().equals(city.getNation()) && city.getCityMove() != null) {
				executeCityMove(unit, city);
			}
		} else {
			executeMoveOrder(unit, unitMove.getCoords());
		}
		unitDao.merge(unit);
	}

	@Override
	public void executeCityMove(Unit unit, City city) {
		executeMoveOrder(unit, city.getCityMove().getCoords());
	}

	private void executeMoveOrder(Unit unit, SectorCoords target) {
		Nation nation = unit.getNation();
		WorldView worldView = sectorDaoService.getAllWorldView(nation);
		if (unit.getMobility() < worldView.distance(unit.getCoords(), target)) {
			return;
		}
		moveUnitFromOrder(unit, target, nation, worldView);
	}

	private void moveUnitFromOrder(Unit unit, SectorCoords target,
			Nation nation, WorldView worldView) {
		List<Unit> units = Lists.newArrayList(unit);
		UnitsToMove unitsToMove = new UnitsToMove(nation,
				AttackType.INITIAL_ATTACK, nation, units,
				target);
		clearUnitMove(unit);
		UnitsMove unitMover = unitCommandFactory.getUnitsMove(unitsToMove,
				worldView);
		@SuppressWarnings("unused") // TODO ENH For now remove the output of this command.  Maybe archive it later.
		Result<MoveCost> result = unitMover.move();
	}
	
	@Override
	public void clearUnitMove(Unit unit) {
		unitDao.clearUnitMove(unit);
	}

	public Unit buildUnit(Nation nation, SectorCoords coords,
			UnitType unitType, Date buildTime) {
		if (UnitBase.isNotUnit(unitType)) {
			return null;
		}
		Unit unit = new Unit(nation, unitType, coords, buildTime);
		unitDao.save(unit);
		UnitBuildAudit unitBuildAudit = new UnitBuildAudit(unit);
		unitDao.persist(unitBuildAudit);

		eventQueue.schedule(unit);
		MoveSeen moveSeen = new MoveSeen(nation, sectorDaoService, this);
		sectorDaoService.updateSeen(dataCache.getWorld(nation.getGameId()),
				unit, moveSeen);
		moveSeen.persist();
		return unit;
	}

	public Unit buildUnit(Nation nation, SectorCoords coords, UnitType unitType) {
		return buildUnit(nation, coords, unitType, new Date());
	}

	public void merge(Unit unit) {
		unitDao.merge(unit);
	}

	public void resupplyAir(Unit carrier) {
		Collection<Unit> units = unitDao.getUnits(carrier.getGame(),
				carrier.getCoords());
		for (Unit unit : units) {
			if (unit.requiresFuel()) {
				unit.resupply();
			}
		}
	}

	public void disable(UnitSeen unitSeen) {
		eventQueue.cancel(unitSeen);
		unitSeen.setEnabled(false);
		unitDao.merge(unitSeen);
	}

	public void remove(Unit unit) {
		eventQueue.cancel(unit);
		unitDao.remove(unit);
	}

	public void remove(UnitSeen unitSeen) {
		eventQueue.cancel(unitSeen);
		unitDao.remove(unitSeen);
	}

	public void saveOrUpdate(Nation nation, Unit unit) {
		if (nation.equals(unit.getNation())) {
			throw new IllegalStateException("Units cannot see themselves");
		}
		if (!unit.isAlive()) {
			return;
		}
		if (unitDao.findUnit(unit.getId()) == null) {
			return;
		}

		UnitSeen unitSeen = new UnitSeen(nation, unit);
		unitSeen = unitDao.saveOrUpdate(unitSeen);
		eventQueue.schedule(unitSeen);
	}

	public void killUnit(Unit unit) {
		eventQueue.cancel(unit);
		clearUnitMove(unit);
		unit.kill();
		unitDao.merge(unit);
		disableUnitSeen(unit);
	}

	public void damage(Unit unit, int damage) {
		unit.damage(damage);
		unitDao.merge(unit);
		if (!unit.isAlive()) {
			eventQueue.cancel(unit);
			disableUnitSeen(unit);
		}
	}

	private void disableUnitSeen(Unit unit) {
		List<UnitSeen> unitsSeen = unitDao.getUnitsSeen(unit);
		for (UnitSeen unitSeen : unitsSeen) {
			disable(unitSeen);
		}
	}

	public void disable(Nation nation, Unit unit) {
		UnitSeen unitSeen = unitDao.findUnitSeen(nation, unit);
		if (unitSeen == null) {
			return;
		}
		disable(unitSeen);
	}

	public void persist(LaunchedSatellite launchedSatellite) {
		unitDao.persist(launchedSatellite);
	}

	public void disable(UnitSeenPK unitSeenPK) {
		UnitSeen unitSeen = unitDao.findUnitSeen(unitSeenPK);
		if (unitSeen == null) {
			return;
		}
		disable(unitSeen);
		Unit unit = unitSeen.getUnit();
		if (unit.isAlive()) {
			MoveSeen moveSeen = new MoveSeen(unit.getNation(),
					sectorDaoService, this);
			sectorDaoService.unitSeen(dataCache.getWorld(unit.getGameId()),
					unit, moveSeen, false);
			moveSeen.persistSeen();
		}
	}

	public void saveOrUpdate(UnitSeen unitSeen) {
		saveOrUpdate(unitSeen.getNation(), unitSeen.getUnit());
	}

	public void disable(Nation nation, List<Unit> units) {
		for (Unit unit : units) {
			disable(nation, unit);
		}
	}

	public Set<Unit> getTeamSeenUnits(Nation nation, Collection<Nation> allies) {
		Set<Unit> units = new HashSet<Unit>();
		Collection<Unit> myUnits = unitDao.getSeenUnits(nation);
		units.addAll(myUnits);
		for (Nation ally : allies) {
			Collection<Unit> allySeenUnits = unitDao.getSeenUnits(ally);
			for (Unit unit : allySeenUnits) {
				if (unit.getNation().equals(nation)) {
					continue;
				}
				units.add(unit);
			}
		}
		return units;
	}

	public Set<Unit> getTeamUnits(Nation nation, Collection<Nation> allies) {
		Set<Unit> units = getAllyUnits(allies);
		units.addAll(unitDao.getUnits(nation));
		units.addAll(getTeamSeenUnits(nation, allies));

		return units;
	}

	public Set<Unit> getAllyUnits(Collection<Nation> allies) {
		Set<Unit> units = new HashSet<Unit>();
		for (Nation ally : allies) {
			List<Unit> allyUnits = unitDao.getUnits(ally);
			units.addAll(allyUnits);
		}
		return units;
	}

	private int getTotalUnitCost(Nation nation) {
		List<Unit> units = unitDao.getUnits(nation);
		int total = 0;
		for (Unit unit : units) {
			if (unit.getType() == UnitType.ZEPPELIN) {
				continue;
			}
			total += unit.getProductionTime();
		}
		return total;
	}

	public Map<SectorCoords, List<Unit>> getUnitMap(Game game) {
		Collection<Unit> units = unitDao.getUnits(game);
		Map<SectorCoords, List<Unit>> retval = new HashMap<SectorCoords, List<Unit>>();
		for (Unit unit : units) {
			SectorCoords coords = unit.getCoords();
			List<Unit> list = retval.get(coords);
			if (list == null) {
				list = new ArrayList<Unit>();
				retval.put(coords, list);
			}
			list.add(unit);
		}
		return retval;
	}

	public int getPower(Nation nation) {
		return (getTotalUnitCost(nation) + 5) / 10;
	}

	public Result<None> cedeUnit(Unit unit, Nation nation) {
		Nation oldOwner = unit.getNation();
		unitDao.transferUnit(unit, nation);
		UnitSeen unitSeen = unitDao.findUnitSeen(nation, unit);
		if (unitSeen != null) {
			unitDao.transferUnitSeen(unitSeen, oldOwner);
		}
		messageDaoService.notify(nation, oldOwner + " " + unit + " ceded",
				oldOwner + " gave you a " + unit + " at " + unit.getCoords());
		return new Result<None>(unit + " ownership transferred from "
				+ oldOwner + " to " + unit.getNation(), true);
	}

	public List<Unit> getPassengers(Unit holder, WorldSector fromSector, Collection<Unit> exclude) {
		Collection<Unit> units = unitDao.getUnits(holder.getGame(),
				fromSector.getCoords());
		List<Unit> passengers = new ContainerUnit(holder, units)
				.getPassengers(fromSector, exclude);
		return passengers;
	}

	public List<Unit> getPassengers(Unit holder, WorldSector fromSector) {
		return getPassengers(holder, fromSector, null);
	}
	
	@Override
	public Result<None> disbandUnit(Unit unit) {
		killUnit(unit);
		return new Result<None>(unit + " destroyed.", true);
	}
	
	@Override
	public Result<None> cancelMoveOrder(Unit unit) {
		clearUnitMove(unit);
		return new Result<None>("Move order cancelled for " +unit+".", true);
	}
	
	@Override
	public Result<None> buildCity(Unit unit) {
		return sectorDaoService.establishCity(unit);
	}

	@Override
	public void updateCarrierRadar(Nation nation) {
		Collection<Unit> carriers = unitDao.getUnitsOfType(nation,
				UnitType.CARRIER);
		int radarRadius = nation.getRadarRadius();
		for (Unit carrier : carriers) {
			carrier.setSight(radarRadius);
			unitDao.merge(carrier);
		}
	}

	@Override
	public void removeUnitsSeen(Game game) {
		for (UnitSeen unitSeen : Lists.newArrayList(unitDao.getUnitsSeen(game))) {
			remove(unitSeen);
		}
	}

	@Override
	public void removeUnitMoves(Game game) {
		for (UnitMove unitMove : Lists.newArrayList(unitDao.getUnitMoves(game))) {
			unitDao.remove(unitMove);
		}
	}

	@Override
	public void setUnitMove(Unit unit, SectorCoords targetCoords) {
		UnitMove unitMove = new UnitMove(unit, targetCoords);
		unit.setUnitMove(unitMove);
		unitDao.persist(unitMove);
		merge(unit);
	}

}
