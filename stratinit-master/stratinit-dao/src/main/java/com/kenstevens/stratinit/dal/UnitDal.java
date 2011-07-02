package com.kenstevens.stratinit.dal;

import java.util.Collection;
import java.util.List;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.LaunchedSatellite;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitMove;
import com.kenstevens.stratinit.model.UnitSeen;
import com.kenstevens.stratinit.model.audit.UnitBuildAudit;

public interface UnitDal {

	void flush(Unit unit);

	List<Unit> getUnits(Game game);

	List<UnitSeen> getUnitsSeen(Game game);

	List<UnitMove> getUnitsMove(Game game);

	void flushUnitsSeen(Collection<UnitSeen> unitsSeen);

	void flushUnitsMove(Collection<UnitMove> unitsMove);

	List<UnitBuildAudit> getBuildAudits(int gameId, String username);

	List<UnitBuildAudit> getBuildAudits(Game game);

	List<LaunchedSatellite> getSatellites(Game game);

	void flushLaunchedSatellites(
			Collection<LaunchedSatellite> launchedSatellites);

	void persist(Unit unit);

	void persist(LaunchedSatellite satellite);

	void saveOrUpdate(UnitSeen unitSeen);

	void persist(UnitBuildAudit unitBuildAudit);

	void remove(Unit unit);

	void remove(UnitSeen unitSeen);

	void flush(UnitSeen unitSeen);

	void flush(UnitMove unitMove);

	void remove(UnitMove unitMove);

	void persist(UnitMove unitMove);

	List<UnitMove> findUnitMoves(Unit unit);

}
