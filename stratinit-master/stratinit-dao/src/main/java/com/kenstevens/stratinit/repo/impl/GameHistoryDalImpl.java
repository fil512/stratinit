package com.kenstevens.stratinit.repo.impl;

import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.model.GameHistoryNation;
import com.kenstevens.stratinit.model.GameHistoryTeam;
import com.kenstevens.stratinit.repo.GameHistoryDal;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@SuppressWarnings("unchecked")
@Service
public class GameHistoryDalImpl implements GameHistoryDal {
	@PersistenceContext
	protected EntityManager entityManager;

	@Override
	public void persist(GameHistory gameHistory) {
		entityManager.persist(gameHistory);
	}

	@Override
	public List<GameHistory> getAllGameHistories() {
		return entityManager.createQuery("from GameHistory")
				.getResultList();
	}

	@Override
	public GameHistory findGameHistory(int gameHistoryId) {
		return entityManager.find(GameHistory.class, gameHistoryId);
	}

	@Override
	public List<GameHistoryTeam> getGameHistoryTeams(GameHistory gameHistory) {
		return entityManager.createQuery(
		"from GameHistoryTeam t where t.gameHistory = :gameHistory").setParameter(
		"gameHistory", gameHistory).getResultList();
	}

	@Override
	public List<GameHistoryNation> getGameHistoryNations(GameHistoryTeam gameHistoryTeam) {
		return entityManager.createQuery(
		"from GameHistoryNation n where n.gameHistoryTeam = :gameHistoryTeam").setParameter(
		"gameHistoryTeam", gameHistoryTeam).getResultList();
	}

	@Override
	public void persist(GameHistoryTeam gameHistoryTeam) {
		entityManager.persist(gameHistoryTeam);
	}

	@Override
	public void persist(GameHistoryNation gameHistoryNation) {
		entityManager.persist(gameHistoryNation);
	}

	@Override
	public GameHistory getGameHistoryByGameId(Integer gameId) {
		List<GameHistory> histories = entityManager.createQuery(
		"from GameHistory g where g.gameId = :gameId").setParameter(
		"gameId", gameId).getResultList();
		if (histories.size() == 1) {
			return histories.get(0);
		}
		return null;
	}

}
