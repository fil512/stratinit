package com.kenstevens.stratinit.repo.impl;

import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.repo.UnitDal;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
@Service
public class UnitDalImpl implements UnitDal {
	@PersistenceContext
	protected EntityManager entityManager;

	@Override
	public void flush(Unit unit) {
		entityManager.merge(unit);
	}

	@Override
	public List<Unit> getUnits(Game game) {
		return entityManager
				.createQuery(
						"from Unit u WHERE u.nation.nationPK.game = :game and u.alive = true")
				.setParameter("game", game).getResultList();
	}

	@Override
	public List<UnitSeen> getUnitsSeen(Game game) {
		return entityManager
				.createQuery(
						"from UnitSeen us where us.unitSeenPK.nation.nationPK.game = :game and us.enabled = true")
				.setParameter("game", game).getResultList();
	}
	
	@Override
	public List<UnitMove> getUnitsMove(Game game) {
		return entityManager
				.createQuery(
						"from UnitMove um where um.unit.nation.nationPK.game = :game")
				.setParameter("game", game).getResultList();
	}
	
	@Override
	public void flushUnitsSeen(Collection<UnitSeen> unitsSeen) {
		for (UnitSeen unitSeen : unitsSeen) {
			entityManager.merge(unitSeen);
		}
	}
	// TODO REF the pattern here needs to be turned into an interface and enforced
	@Override
	public void flushUnitsMove(Collection<UnitMove> unitsMove) {
		for (UnitMove unitMove : unitsMove) {
			entityManager.merge(unitMove);
		}
	}
	
	@Override
	public List<UnitBuildAudit> getBuildAudits(int gameId, String username) {
		return entityManager
				.createQuery(
						"from UnitBuildAudit u WHERE u.gameId = :gameId and u.username = :username")
				.setParameter("gameId", gameId).setParameter("username",
						username).getResultList();
	}
	
	@Override
	public List<UnitBuildAudit> getBuildAudits(Game game) {
		return getBuildAudits(game.getId());
	}

	@Override
	public List<UnitBuildAudit> getBuildAudits(int gameId) {
			return entityManager
					.createQuery(
							"from UnitBuildAudit u WHERE u.gameId = :gameId")
					.setParameter("gameId", gameId).getResultList();
	}
	
	@Override
	public List<LaunchedSatellite> getSatellites(Game game) {
		return entityManager.createQuery(
				"from LaunchedSatellite u WHERE u.nation.nationPK.game = :game")
				.setParameter("game", game).getResultList();
	}
	
	@Override
	public void flushLaunchedSatellites(Collection<LaunchedSatellite> launchedSatellites) {
		for (LaunchedSatellite launchedSatellite : launchedSatellites) {
			entityManager.merge(launchedSatellite);
		}
	}
	
	@Override
	public void persist(Unit unit) {
		entityManager.persist(unit);
	}

	@Override
	public void persist(UnitMove unitMove) {
		entityManager.persist(unitMove);
	}

	@Override
	public void persist(LaunchedSatellite satellite) {
		entityManager.persist(satellite);
	}

	@Override
	public void saveOrUpdate(UnitSeen unitSeen) {
		UnitSeen foundUnitSeen = entityManager.find(UnitSeen.class, unitSeen.getUnitSeenPK());
		if (foundUnitSeen == null) {
			entityManager.persist(unitSeen);
		} else {
			foundUnitSeen.resetExpiry();
			foundUnitSeen.setEnabled(true);
			entityManager.merge(foundUnitSeen);
		}
	}

	@Override
	public void persist(UnitBuildAudit unitBuildAudit) {
		entityManager.persist(unitBuildAudit);
	}

	@Override
	public void remove(Unit unit) {
		if (unit == null || unit.getId() == null) {
			return;
		}
		// TODO REF use cascade delete here
		List<UnitSeen> unitsSeen = entityManager.createQuery(
				"from UnitSeen us WHERE us.unitSeenPK.unit = :unit")
				.setParameter("unit", unit).getResultList();
		for (UnitSeen unitSeen : unitsSeen) {
			entityManager.remove(unitSeen);
		}
		removeUnit(unit.getId());
	}
	
	private void removeUnit(Integer unitId) {
		Unit unit = entityManager.find(Unit.class, unitId);
		if (unit != null) {
			entityManager.remove(unit);
		}
	}

	@Override
	public void remove(UnitSeen unitSeen) {
		if (unitSeen == null || unitSeen.getUnitSeenPK() == null) {
			return;
		}
		removeUnitSeen(unitSeen.getUnitSeenPK());
	}
	
	private void removeUnitSeen(UnitSeenPK unitSeenPK) {
		UnitSeen unitSeen = entityManager.find(UnitSeen.class, unitSeenPK);
		if (unitSeen != null) {
			entityManager.remove(unitSeen);
		}
	}
	
	@Override
	public void remove(UnitMove unitMove) {
		if (unitMove == null || unitMove.getUnit() == null) {
			return;
		}
		removeUnitMove(unitMove.getId());
	}
	
	private void removeUnitMove(int id) {
		UnitMove unitMove = entityManager.find(UnitMove.class, id);
		if (unitMove != null) {
			entityManager.remove(unitMove);
		}
	}
	
	

	@Override
	public void flush(UnitSeen unitSeen) {
		entityManager.merge(unitSeen);
	}

	@Override
	public void flush(UnitMove unitMove) {
		entityManager.merge(unitMove);
		
	}

	@Override
	public List<UnitMove> findUnitMoves(Unit unit) {
		return entityManager
				.createQuery(
						"from UnitMove um where um.unit = :unit")
				.setParameter("unit", unit).getResultList();
	}
}
