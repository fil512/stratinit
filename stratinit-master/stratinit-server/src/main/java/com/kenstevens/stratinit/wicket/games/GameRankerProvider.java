package com.kenstevens.stratinit.wicket.games;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dal.GameHistoryDal;
import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.rank.TeamRanker;
import com.kenstevens.stratinit.rank.TeamHelper;
import com.kenstevens.stratinit.rank.TeamProvider;

@Service
public class GameRankerProvider {
	@Autowired
	TeamProvider teamProvider;
	@Autowired
	GameHistoryDal gameHistoryDal;
	
	public TeamRanker getGameRanker() {
		TeamHelper teamHelper = new TeamHelper(teamProvider);
		TeamRanker gameRanker = new TeamRanker(teamHelper);
		List<GameHistory> games = gameHistoryDal.getAllGameHistories();
		for (GameHistory game : games) {
			gameRanker.rank(game);
		}
		return gameRanker;
	}
}
