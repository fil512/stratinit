package com.kenstevens.stratinit.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dao.LogDao;
import com.kenstevens.stratinit.model.BattleLog;
import com.kenstevens.stratinit.model.CityCapturedBattleLog;
import com.kenstevens.stratinit.model.CityNukedBattleLog;
import com.kenstevens.stratinit.model.ErrorLog;
import com.kenstevens.stratinit.model.FlakBattleLog;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.UnitAttackedBattleLog;

@SuppressWarnings("unchecked")
@Service
public class LogDaoImpl implements LogDao {
	@PersistenceContext
	protected EntityManager entityManager;

	@Override
	public void persist(BattleLog battleLog) {
		entityManager.persist(battleLog);
	}

	@Override
	public List<CityCapturedBattleLog> getCityCapturedBattleLogs(Nation nation) {
		return entityManager
				.createQuery(
						"from CityCapturedBattleLog b WHERE b.attacker = :nation or b.defender = :nation")
				.setParameter("nation", nation).getResultList();
	}

	@Override
	public List<UnitAttackedBattleLog> getUnitAttackedBattleLogs(Nation nation) {
		return entityManager
				.createQuery(
						"from UnitAttackedBattleLog b WHERE b.attacker = :nation or b.defender = :nation")
				.setParameter("nation", nation).getResultList();
	}

	@Override
	public List<FlakBattleLog> getFlakBattleLogs(Nation nation) {
		return entityManager
				.createQuery(
						"from FlakBattleLog b WHERE b.attacker = :nation or b.defender = :nation")
				.setParameter("nation", nation).getResultList();
	}

	@Override
	public void remove(CityCapturedBattleLog log) {
		if (log == null || log.getId() == null) {
			return;
		}
		Integer battleLogId = log.getId();
		CityCapturedBattleLog foundLog = entityManager.find(CityCapturedBattleLog.class, battleLogId);
		if (foundLog != null) {
			entityManager.remove(foundLog);
			return;
		}
	}

	@Override
	public void remove(UnitAttackedBattleLog log) {
		if (log == null || log.getId() == null) {
			return;
		}
		Integer battleLogId = log.getId();
		UnitAttackedBattleLog foundLog = entityManager.find(UnitAttackedBattleLog.class, battleLogId);
		if (foundLog != null) {
			entityManager.remove(foundLog);
			return;
		}
	}


	@Override
	public void remove(FlakBattleLog log) {
		if (log == null || log.getId() == null) {
			return;
		}
		Integer battleLogId = log.getId();
		FlakBattleLog foundLog = entityManager.find(FlakBattleLog.class, battleLogId);
		if (foundLog != null) {
			entityManager.remove(foundLog);
			return;
		}
	}

	@Override
	public void remove(CityNukedBattleLog log) {
		if (log == null || log.getId() == null) {
			return;
		}
		Integer battleLogId = log.getId();
		CityNukedBattleLog foundLog = entityManager.find(CityNukedBattleLog.class, battleLogId);
		if (foundLog != null) {
			entityManager.remove(foundLog);
			return;
		}
	}

	@Override
	public void removeLogs(Game game) {
		List<BattleLog> logs = getBattleLogs(game);
		for (BattleLog log : logs) {
			entityManager.remove(log);
		}
	}

	@Override
	public List<BattleLog> getBattleLogs(Game game) {
		List<BattleLog> logs = new ArrayList<BattleLog>();
		logs.addAll(getCityCapturedBattleLogs(game));
		logs.addAll(getUnitAttackedBattleLogs(game));
		logs.addAll(getFlakBattleLogs(game));
		logs.addAll(getCityNukedBattleLogs(game));
		return logs;
	}

	@Override
	public List<CityCapturedBattleLog> getCityCapturedBattleLogs(Game game) {
		return entityManager
		.createQuery(
				"from CityCapturedBattleLog b WHERE b.attacker.nationPK.game = :game")
		.setParameter("game", game).getResultList();
	}

	@Override
	public List<UnitAttackedBattleLog> getUnitAttackedBattleLogs(Game game) {
		return entityManager
		.createQuery(
				"from UnitAttackedBattleLog b WHERE b.attacker.nationPK.game = :game")
		.setParameter("game", game).getResultList();
	}

	@Override
	public List<FlakBattleLog> getFlakBattleLogs(Game game) {
		return entityManager
		.createQuery(
				"from FlakBattleLog b WHERE b.attacker.nationPK.game = :game")
		.setParameter("game", game).getResultList();
	}

	@Override
	public List<CityNukedBattleLog> getCityNukedBattleLogs(Game game) {
		return entityManager
		.createQuery(
				"from CityNukedBattleLog b WHERE b.attacker.nationPK.game = :game")
		.setParameter("game", game).getResultList();
	}

	@Override
	public void persist(ErrorLog errorLog) {
		entityManager.persist(errorLog);
	}
}
