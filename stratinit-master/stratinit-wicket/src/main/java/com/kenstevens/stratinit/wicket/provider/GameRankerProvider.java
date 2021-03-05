package com.kenstevens.stratinit.wicket.provider;

import com.kenstevens.stratinit.client.model.GameHistory;
import com.kenstevens.stratinit.rank.TeamHelper;
import com.kenstevens.stratinit.rank.TeamProvider;
import com.kenstevens.stratinit.rank.TeamRanker;
import com.kenstevens.stratinit.repo.GameHistoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameRankerProvider {
	@Autowired
	TeamProvider teamProvider;
	@Autowired
	GameHistoryRepo gameHistoryRepo;
	
	public TeamRanker getGameRanker() {
		TeamHelper teamHelper = new TeamHelper(teamProvider);
		TeamRanker teamRanker = new TeamRanker(teamHelper);
		List<GameHistory> games = gameHistoryRepo.findAll();
		for (GameHistory game : games) {
			teamRanker.rank(game);
		}
		return teamRanker;
	}
}
