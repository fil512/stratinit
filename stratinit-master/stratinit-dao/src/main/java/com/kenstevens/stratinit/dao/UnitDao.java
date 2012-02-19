package com.kenstevens.stratinit.dao;

import java.util.Collection;
import java.util.List;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.LaunchedSatellite;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitMove;
import com.kenstevens.stratinit.model.UnitSeen;
import com.kenstevens.stratinit.model.UnitSeenPK;
import com.kenstevens.stratinit.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public interface UnitDao {

	public abstract void persist(Unit unit);

	public abstract void persist(LaunchedSatellite satellite);

	public abstract void persist(UnitSeen unitSeen);

	public abstract List<Unit> getUnits(Nation nation);

	public abstract Collection<LaunchedSatellite> getSatellites(Nation nation);

	public abstract List<Unit> getAllUnits();

	public abstract Collection<Unit> getSeenUnits(Nation nation);

	public abstract Collection<Unit> getUnits(Game game, SectorCoords coords);

	public abstract Collection<Unit> getUnits(Game game);

	public abstract Unit findUnit(Integer unitId);

	public abstract void remove(Unit unit);

	public abstract void merge(Unit unit);

	public abstract Collection<Unit> getUnits(Sector sector);

	public abstract void merge(UnitSeen unitSeen);

	public abstract UnitSeen findUnitSeen(Nation nation, Unit unit);

	public abstract UnitSeen findUnitSeen(UnitSeenPK unitSeenPK);

	public abstract UnitSeen saveOrUpdate(UnitSeen unitSeen);

	public abstract Collection<Unit> getUnitsWithin(CoordMeasure coordMeasure,
			Nation nation, SectorCoords coords, int distance);

	public abstract List<UnitBuildAudit> getBuildAudits(int gameId,
			String username);

	public abstract Collection<Nation> getOtherNationsThatCanSeeThisSub(CoordMeasure coordMeasure, Unit unit);

	public abstract Collection<Nation> getOtherNationsThatCanSeeThisUnit(CoordMeasure coordMeasure, Unit unit);

	public abstract Collection<Unit> getUnitsThatCanInterdictThisUnit(CoordMeasure coordMeasure,
			Nation nation, Unit unit);

	public abstract Collection<Unit> getUnitsThatCanCounterFireThisUnit(CoordMeasure coordMeasure,
			Nation nation, Unit unit, SectorCoords excludeCoords);

	public abstract Collection<Nation> getOtherNationsThatSeeThisUnit(Unit unit);

	public abstract void removeUnits(Game game);

	public abstract List<UnitSeen> getUnitsSeen(Game game);

	public abstract List<UnitMove> getUnitMoves(Game game);

	public abstract void remove(UnitSeen unitSeen);

	public abstract List<UnitBuildAudit> getBuildAudits(Game game);

	public abstract void persist(UnitBuildAudit unitBuildAudit);

	public abstract List<UnitSeen> getUnitsSeen(Unit unit);

	public abstract int getNumberOfCapitalShips(Nation nation);

	public abstract void transferUnit(Unit unit, Nation nation);

	public abstract void transferUnitSeen(UnitSeen unitSeen, Nation oldOwner);

	public abstract Collection<Unit> getUnitsOfType(Nation nation,
			UnitType unitType);

	public abstract Collection<Unit> getMissiles(Nation nation);

	void merge(UnitMove unitMove);

	void persist(UnitMove unitMove);

	void remove(UnitMove unitMove);

	void clearUnitMove(Unit unit);

	public abstract boolean isAnEscort(CoordMeasure coordMeasure, Unit unit);

}