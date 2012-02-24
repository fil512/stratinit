package com.kenstevens.stratinit.wicket.games;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dal.GameHistoryDal;
import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.rank.GameRanker;
import com.kenstevens.stratinit.rank.TeamHelper;
import com.kenstevens.stratinit.rank.TeamProvider;

@Service
public class GameRankerProvider {
	@Autowired
	TeamProvider teamProvider;
	@Autowired
	GameHistoryDal gameHistoryDal;
	
	public GameRanker getGameRanker() {
		TeamHelper teamHelper = new TeamHelper(teamProvider);
		GameRanker gameRanker = new GameRanker(teamHelper);
		List<GameHistory> games = gameHistoryDal.getAllGameHistories();
		for (GameHistory game : games) {
			gameRanker.rank(game);
		}
		return gameRanker;
	}
}
