package com.kenstevens.stratinit.wicket.games;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dal.GameHistoryDal;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.rank.TeamProvider;

@Service
public class GameArchiveListProvider implements GameListProvider {
	@Autowired
	GameHistoryDal gameHistoryDal;
	@Autowired
	TeamProvider teamProvider;

	@Override
	public List<GameTable> getGameTableList() {
		List<GameTable> retval = new ArrayList<GameTable>();
		List<GameHistory> games = gameHistoryDal.getAllGameHistories();
		for (GameHistory gameHistory : games) {
			GameTable gameTable = new GameTable(gameHistory);
			setTeams(gameHistory, gameTable);
			retval.add(gameTable);
		}
		return retval;
	}

	private void setTeams(GameHistory gameHistory, GameTable gameTable) {
		List<SITeam> teams = Lists.newArrayList();
		List<SINation> nations = Lists.newArrayList();
		teamProvider.getTeamsAndNations(gameHistory, teams, nations);
		
		gameTable.setTeams(teams);
		gameTable.setNations(nations);
	}
}
