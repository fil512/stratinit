package com.kenstevens.stratinit.server.daoservice;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.LaunchedSatellite;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitSeen;
import com.kenstevens.stratinit.model.UnitSeenPK;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public interface UnitDaoService {

	void updateUnit(Unit unit, Date buildTime);

	Unit buildUnit(Nation nation, SectorCoords coords,
			UnitType unitType, Date buildTime);

	Unit buildUnit(Nation nation, SectorCoords coords,
			UnitType unitType);

	void merge(Unit unit);

	void resupplyAir(Unit carrier);

	void disable(UnitSeen unitSeen);

	void remove(Unit unit);

	void remove(UnitSeen unitSeen);

	void saveOrUpdate(Nation nation, Unit unit);

	void killUnit(Unit unit);

	void damage(Unit unit, int damage);

	void disable(Nation nation, Unit unit);

	void persist(LaunchedSatellite launchedSatellite);

	void disable(UnitSeenPK unitSeenPK);

	void saveOrUpdate(UnitSeen unitSeen);

	void disable(Nation nation, List<Unit> units);

	Set<Unit> getTeamSeenUnits(Nation nation,
			Collection<Nation> allies);

	Set<Unit> getTeamUnits(Nation nation,
			Collection<Nation> allies);

	Set<Unit> getAllyUnits(Collection<Nation> allies);

	Map<SectorCoords, List<Unit>> getUnitMap(Game game);

	int getPower(Nation nation);

	Result<None> cedeUnit(Unit unit, Nation nation);

	List<Unit> getPassengers(Unit holder,
			WorldSector holderSector);

	Result<None> disbandUnit(Unit unit);

	void updateCarrierRadar(Nation nation);

	void removeUnitsSeen(Game game);

	void removeUnitMoves(Game game);

	void setUnitMove(Unit unit, SectorCoords targetCoords);

	void clearUnitMove(Unit unit);

	Result<None> buildCity(Unit unit);

	Result<None> cancelMoveOrder(Unit unit);

	void executeCityMove(Unit unit, City city);

	List<Unit> getPassengers(Unit unit,
			WorldSector worldSector, Collection<Unit> values);
}