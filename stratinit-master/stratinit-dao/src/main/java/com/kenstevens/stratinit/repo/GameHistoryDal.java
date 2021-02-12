package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.model.GameHistoryNation;
import com.kenstevens.stratinit.model.GameHistoryTeam;

import java.util.List;

public interface GameHistoryDal {
	GameHistory findGameHistory(int gameHistoryId);
	List<GameHistoryTeam> getGameHistoryTeams(GameHistory gameHistory);
	List<GameHistoryNation> getGameHistoryNations(GameHistoryTeam gameHistoryTeam);
	void persist(GameHistory gameHistory);
	void persist(GameHistoryTeam gameHistoryTeam);
	void persist(GameHistoryNation gameHistoryNation);
	List<GameHistory> getAllGameHistories();
	GameHistory getGameHistoryByGameId(Integer gameId);
}
