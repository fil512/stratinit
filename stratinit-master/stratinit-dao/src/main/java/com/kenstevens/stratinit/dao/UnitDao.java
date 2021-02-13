package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

import java.util.Collection;
import java.util.List;

public interface UnitDao {

    void save(Unit unit);

    void persist(LaunchedSatellite satellite);

    void save(UnitSeen unitSeen);

    List<Unit> getUnits(Nation nation);

    Collection<LaunchedSatellite> getSatellites(Nation nation);

    List<Unit> getAllUnits();

    Collection<Unit> getSeenUnits(Nation nation);

    Collection<Unit> getUnits(Game game, SectorCoords coords);

    Collection<Unit> getUnits(Game game);

    Unit findUnit(Integer unitId);

    void delete(Unit unit);

    void merge(Unit unit);

    Collection<Unit> getUnits(Sector sector);

    void merge(UnitSeen unitSeen);

    UnitSeen findUnitSeen(Nation nation, Unit unit);

    UnitSeen findUnitSeen(UnitSeenPK unitSeenPK);

	UnitSeen saveOrUpdate(UnitSeen unitSeen);

	Collection<Unit> getUnitsWithin(CoordMeasure coordMeasure,
			Nation nation, SectorCoords coords, int distance);

	List<UnitBuildAudit> getBuildAudits(int gameId,
			String username);

	Collection<Nation> getOtherNationsThatCanSeeThisSub(CoordMeasure coordMeasure, Unit unit);

	Collection<Nation> getOtherNationsThatCanSeeThisUnit(CoordMeasure coordMeasure, Unit unit);

	Collection<Unit> getUnitsThatCanInterdictThisUnit(CoordMeasure coordMeasure,
			Nation nation, Unit unit);

	Collection<Unit> getUnitsThatCanCounterFireThisUnit(CoordMeasure coordMeasure,
			Nation nation, Unit unit, SectorCoords excludeCoords);

	Collection<Nation> getOtherNationsThatSeeThisUnit(Unit unit);

	void removeUnits(Game game);

	List<UnitSeen> getUnitsSeen(Game game);

	List<UnitMove> getUnitMoves(Game game);

	void remove(UnitSeen unitSeen);

	List<UnitBuildAudit> getBuildAudits(Game game);

	void persist(UnitBuildAudit unitBuildAudit);

	List<UnitSeen> getUnitsSeen(Unit unit);

	int getNumberOfCapitalShips(Nation nation);

	void transferUnit(Unit unit, Nation nation);

	void transferUnitSeen(UnitSeen unitSeen, Nation oldOwner);

	Collection<Unit> getUnitsOfType(Nation nation,
			UnitType unitType);

	Collection<Unit> getMissiles(Nation nation);

	void merge(UnitMove unitMove);

	void persist(UnitMove unitMove);

	void remove(UnitMove unitMove);

	void clearUnitMove(Unit unit);

	boolean isAnEscort(CoordMeasure coordMeasure, Unit unit);

}