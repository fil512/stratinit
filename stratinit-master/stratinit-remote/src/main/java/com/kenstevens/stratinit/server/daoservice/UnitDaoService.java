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

	public abstract void updateUnit(Unit unit, Date buildTime);

	public abstract Unit buildUnit(Nation nation, SectorCoords coords,
			UnitType unitType, Date buildTime);

	public abstract Unit buildUnit(Nation nation, SectorCoords coords,
			UnitType unitType);

	public abstract void merge(Unit unit);

	public abstract void resupplyAir(Unit carrier);

	public abstract void disable(UnitSeen unitSeen);

	public abstract void remove(Unit unit);

	public abstract void remove(UnitSeen unitSeen);

	public abstract void saveOrUpdate(Nation nation, Unit unit);

	public abstract void killUnit(Unit unit);

	public abstract void damage(Unit unit, int damage);

	public abstract void disable(Nation nation, Unit unit);

	public abstract void persist(LaunchedSatellite launchedSatellite);

	public abstract void disable(UnitSeenPK unitSeenPK);

	public abstract void saveOrUpdate(UnitSeen unitSeen);

	public abstract void disable(Nation nation, List<Unit> units);

	public abstract Set<Unit> getTeamSeenUnits(Nation nation,
			Collection<Nation> allies);

	public abstract Set<Unit> getTeamUnits(Nation nation,
			Collection<Nation> allies);

	public abstract Set<Unit> getAllyUnits(Collection<Nation> allies);

	public abstract Map<SectorCoords, List<Unit>> getUnitMap(Game game);

	public abstract int getPower(Nation nation);

	public abstract Result<None> cedeUnit(Unit unit, Nation nation);

	public abstract List<Unit> getPassengers(Unit holder,
			WorldSector holderSector);

	public abstract Result<None> disbandUnit(Unit unit);

	public abstract void updateCarrierRadar(Nation nation);

	public abstract void removeUnitsSeen(Game game);

	public abstract void removeUnitMoves(Game game);

	public abstract void setUnitMove(Unit unit, SectorCoords targetCoords);

	void clearUnitMove(Unit unit);

	Result<None> buildCity(Unit unit);

	public abstract Result<None> cancelMoveOrder(Unit unit);

	void executeCityMove(Unit unit, City city);
}