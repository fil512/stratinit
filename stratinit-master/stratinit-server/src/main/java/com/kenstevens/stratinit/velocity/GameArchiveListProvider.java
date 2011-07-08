package com.kenstevens.stratinit.velocity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dal.GameHistoryDal;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.model.GameHistoryNation;
import com.kenstevens.stratinit.model.GameHistoryTeam;

@Service
public class GameArchiveListProvider implements GameListProvider {
	@Autowired
	GameHistoryDal gameHistoryDal;

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
		List<GameHistoryTeam> gameHistoryTeams = gameHistoryDal.getGameHistoryTeams(gameHistory);
		for (GameHistoryTeam gameHistoryTeam : gameHistoryTeams) {
			List<GameHistoryNation> gameHistoryNations = gameHistoryDal.getGameHistoryNations(gameHistoryTeam);
			addTeam(teams, gameHistoryNations);
			addNations(nations, gameHistoryNations);
		}
		gameTable.setTeams(teams);
		gameTable.setNations(nations);
	}

	private void addNations(List<SINation> nations,
			List<GameHistoryNation> gameHistoryNations) {
		for (GameHistoryNation gameHistoryNation : gameHistoryNations) {
			SINation nation = new SINation();
			nation.cities = gameHistoryNation.getCities();
			nation.name = gameHistoryNation.getName();
			nation.power = gameHistoryNation.getPower();
			nations.add(nation);
		}

	}

	private void addTeam(List<SITeam> teams,
			List<GameHistoryNation> gameHistoryNations) {
		SITeam team = new SITeam();
		team.nation1 = gameHistoryNations.get(0).getName();
		team.score += gameHistoryNations.get(0).getCities();
		if (gameHistoryNations.size() > 1) {
			team.nation2 = gameHistoryNations.get(1).getName();
			team.score += gameHistoryNations.get(1).getCities();
		}
		teams.add(team);
	}
}
