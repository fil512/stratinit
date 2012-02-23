package com.kenstevens.stratinit.rank;

import java.util.Collection;
import java.util.List;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.model.GameHistoryNation;
import com.kenstevens.stratinit.model.GameHistoryTeam;
import com.kenstevens.stratinit.model.Nation;

public class TestTeamHistoryProvider implements TeamProvider {
	public static GameHistoryNation nation1a;
	public static GameHistoryNation nation1b;
	public static GameHistoryNation nation1c;
	public static GameHistoryNation nation1d;
	public static GameHistoryNation nation2a;
	public static GameHistoryNation nation2b;
	public static GameHistoryNation nation2c;
	public static GameHistoryNation nation2e;
	public static GameHistoryNation nation2f;

	static {
		Game game1 = new Game("game1");
		game1.setId(1);
		GameHistory gameHist1 = new GameHistory(game1);
		Game game2 = new Game("game2");
		game2.setId(2);
		GameHistory gameHist2 = new GameHistory(game2);
		GameHistoryTeam team1x = new GameHistoryTeam(gameHist1);
		GameHistoryTeam team1y = new GameHistoryTeam(gameHist1);
		GameHistoryTeam team2x = new GameHistoryTeam(gameHist2);
		GameHistoryTeam team2y = new GameHistoryTeam(gameHist2);
		GameHistoryTeam team2z = new GameHistoryTeam(gameHist2);
		
		nation1a = new GameHistoryNation(team1x, "playera", 10, 10);
		nation1b = new GameHistoryNation(team1x, "playerb", 10, 10);
		nation1c = new GameHistoryNation(team1y, "playerc", 10, 10);
		nation1d = new GameHistoryNation(team1y, "playerd", 10, 10);
		nation2a = new GameHistoryNation(team2x, "playera", 10, 10);
		nation2b = new GameHistoryNation(team2x, "playerb", 10, 10);
		nation2c = new GameHistoryNation(team2y, "playerc", 10, 10);
		nation2e = new GameHistoryNation(team2z, "playere", 10, 10);
		nation2f = new GameHistoryNation(team2z, "playerf", 10, 10);
	}

	@Override
	public Collection<Nation> getAllies(Nation nation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Nation> getNations(Game game) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfCities(Nation nation) {
		// TODO Auto-generated method stub
		return 0;
	}

}
