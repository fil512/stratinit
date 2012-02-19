package com.kenstevens.stratinit.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dal.GameDal;
import com.kenstevens.stratinit.dal.SectorDal;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.CityMove;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.LaunchedSatellite;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.model.RelationPK;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.SectorSeen;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitMove;
import com.kenstevens.stratinit.model.UnitSeen;
import com.kenstevens.stratinit.model.World;
import com.kenstevens.stratinit.type.SectorCoords;

public class GameCache extends Cacheable {
	private final Log log = LogFactory.getLog(getClass());

	private final Game game;
	private WorldCache worldCache;
	private final List<NationCache> nationCaches = new ArrayList<NationCache>();
	private final List<Relation> relations;
	private final Map<SectorCoords, City> cityMap = new HashMap<SectorCoords, City>();
	private final Map<Integer, Unit> unitMap = new TreeMap<Integer, Unit>();

	public GameCache(Game game, List<Relation> relations) {
		this.game = game;
		this.relations = relations;
	}

	public int getGameId() {
		return game.getId();
	}

	public void setWorld(World world) {
		this.worldCache = new WorldCache(world);
	}

	public World getWorld() {
		return worldCache.getWorld();
	}

	public void setWorldModified(boolean modified) {
		worldCache.setModified(modified);
	}

	private NationCache getNationCache(Player player) {
		for (NationCache nationCache : nationCaches) {
			if (nationCache.getNation().getPlayer().equals(player)) {
				return nationCache;
			}
		}
		return null;
	}

	public NationCache getNationCache(Nation nation) {
		for (NationCache nationCache : nationCaches) {
			if (nationCache.getNation().equals(nation)) {
				return nationCache;
			}
		}
		return null;
	}

	public List<NationCache> getNationCaches() {
		return Collections.unmodifiableList(nationCaches);
	}

	public Nation getNation(Player player) {
		NationCache nationCache = getNationCache(player);
		if (nationCache == null) {
			return null;
		}
		return nationCache.getNation();
	}

	public void addNations(List<Nation> nations) {
		for (Nation nation : nations) {
			add(nation);
		}
	}

	public void add(Nation nation) {
		this.nationCaches.add(new NationCache(nation));
	}

	public List<Nation> getNations() {
		return Lists.transform(nationCaches, new NationCacheToNationFunction());
	}

	public Game getGame() {
		return game;
	}

	public List<Relation> getRelations() {
		return Collections.unmodifiableList(relations);
	}

	public Relation findRelation(RelationPK relationPK) {
		for (Relation relation : relations) {
			if (relation.getRelationPK().equals(relationPK)) {
				return relation;
			}
		}
		return null;
	}

	public City getCity(SectorCoords coords) {
		return cityMap.get(coords);
	}

	public void add(City city) {
		cityMap.put(city.getCoords(), city);
		getNationCache(city.getNation()).add(city);
	}

	public void remove(City city) {
		cityMap.remove(city.getCoords());
		getNationCache(city.getNation()).remove(city);
	}

	public void setSectorsSeen(List<SectorSeen> sectorsSeen) {
		for (SectorSeen sectorSeen : sectorsSeen) {
			getNationCache(sectorSeen.getNation()).add(sectorSeen);
		}
	}

	public Collection<City> getCities() {
		return cityMap.values();
	}

	public Map<SectorCoords, City> getCityMap() {
		return Collections.unmodifiableMap(cityMap);
	}

	public List<Sector> getSectors() {
		return worldCache.getSectors();
	}

	public void add(Relation relation) {
		relations.add(relation);
	}

	public void transferCity(City city, Nation nation) {
		getNationCache(city.getNation()).remove(city);
		city.setNation(nation);
		getNationCache(city.getNation()).add(city);
	}

	public void transferUnit(Unit unit, Nation nation) {
		getNationCache(unit.getNation()).remove(unit);
		unit.setNation(nation);
		getNationCache(unit.getNation()).add(unit);
	}

	public void add(Unit unit) {
		unitMap.put(unit.getId(), unit);
		getNationCache(unit.getNation()).add(unit);
	}

	public Unit getUnit(int unitId) {
		return unitMap.get(unitId);
	}

	public void setUnitsSeen(List<UnitSeen> unitsSeen) {
		for (UnitSeen unitSeen : unitsSeen) {
			getNationCache(unitSeen.getNation()).add(unitSeen);
		}
	}

	public List<UnitMove> setUnitsMove(List<UnitMove> unitsMove) {
		List<UnitMove> badUnitMoves = Lists.newArrayList();
		for (UnitMove unitMove : unitsMove) {
			Unit unitMoveUnit = unitMove.getUnit();
			if (unitMoveUnit == null) {
				log.error("unitMove.unit is null on unitMove with id "+unitMove.getId());
				badUnitMoves.add(unitMove);
				continue;
			}
			
			Unit unit = getUnit(unitMoveUnit.getId());
			if (unit == null) {
				log.error("Unable to find unit for unitMove with unit id "+unitMoveUnit.getId());
				badUnitMoves.add(unitMove);
				continue;
			} else if (unit.getUnitMove() != null) {
				log.error("More than one unitMove with same unit id "+unitMoveUnit.getId());
				badUnitMoves.add(unitMove);
				continue;
			}
			NationCache nationCache = getNationCache(unitMoveUnit.getNation());
			nationCache.add(unitMove);
			unit.setUnitMove(unitMove);
		}
		return badUnitMoves;
	}

	public List<CityMove> setCityMoves(List<CityMove> cityMoves) {
		List<CityMove> badCityMoves = Lists.newArrayList();
		for (CityMove cityMove : cityMoves) {
			City cityMoveCity = cityMove.getCity();
			if (cityMoveCity == null) {
				log.error("cityMove.city is null on cityMove with id "+cityMove.getId());
				badCityMoves.add(cityMove);
				continue;
			}
			
			City city = getCity(cityMoveCity.getCoords());
			if (city == null) {
				log.error("Unable to find city for cityMove with city "+cityMoveCity.getCoords());
				badCityMoves.add(cityMove);
				continue;
			} else if (city.getCityMove() != null) {
				log.error("More than one cityMove with same city "+cityMoveCity.getCoords());
				badCityMoves.add(cityMove);
				continue;
			}
			NationCache nationCache = getNationCache(cityMoveCity.getNation());
			nationCache.add(cityMove);
			city.setCityMove(cityMove);
		}
		return badCityMoves;
	}

	public Collection<Unit> getUnits() {
		return unitMap.values();
	}

	public void setLaunchedSatellites(List<LaunchedSatellite> satellites) {
		for (LaunchedSatellite launchedSatellite : satellites) {
			getNationCache(launchedSatellite.getNation())
					.add(launchedSatellite);
		}
	}

	public void remove(Unit unit) {
		unitMap.remove(unit.getId());
		getNationCache(unit.getNation()).remove(unit);
	}

	public void flush(GameDal gameDal, SectorDal sectorDal) {
		if (isModified()) {
			log.info("Flushing game #" + getGameId());
			gameDal.flush(getGame());
			gameDal.flushRelations(getRelations());
			setModified(false);
		}
		if (worldCache != null && worldCache.isModified()) {
			log.info("Flushing world for game #" + getGameId());
			sectorDal.flushSectors(getSectors());
			setWorldModified(false);
		}
	}
	
	public boolean isWorldModified() {
		return worldCache.isModified();
	}
}
