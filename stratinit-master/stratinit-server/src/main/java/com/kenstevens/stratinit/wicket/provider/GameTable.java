package com.kenstevens.stratinit.wicket.provider;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.GameHistory;

public class GameTable {
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("d MMM, yyyy");
	
	Comparator<SITeam> teamsByScoreComparator = new Comparator<SITeam>() {
		@Override
		public int compare(SITeam team1, SITeam team2) {
			return Integer.valueOf(team2.score).compareTo(team1.score);
		}
	};
	Comparator<SINation> nationsByPowerComparator = new Comparator<SINation>() {
		@Override
		public int compare(SINation nation1, SINation nation2) {
			return Integer.valueOf(nation2.cities).compareTo(nation1.cities);
		}
	};


	private final GameHistory gameHistory;
	private List<SITeam> teams;
	private List<SINation> nations;

	public String getName() {
		return gameHistory.getName();
	}
	
	public String getStarts() {
		return FORMAT.format(gameHistory.getStartTime());
	}
	
	public String getEnds() {
		return FORMAT.format(gameHistory.getEnds());
	}
	
	public int getId() {
		return gameHistory.getGameId();
	}
	
	public List<SITeam> getTeams() {
		return teams;
	}
	
	public GameTable(Game game) {
		this.gameHistory = new GameHistory(game);
	}

	public GameTable(GameHistory gameHistory) {
		this.gameHistory = gameHistory;
	}

	public void setTeams(List<SITeam> teams) {
		Collections.sort(teams, teamsByScoreComparator);
		this.teams = teams;
	}

	public void setNations(List<SINation> nations) {
		Collections.sort(nations, nationsByPowerComparator);
		this.nations = nations;
	}

	public List<SINation> getNations() {
		return nations;
	}

	public boolean hasEnded() {
		Date now = new Date();
		return gameHistory.getEnds().before(now);
	}


}