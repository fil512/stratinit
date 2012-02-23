package com.kenstevens.stratinit.rank;

import java.util.Map.Entry;
import java.util.SortedMap;

import org.junit.Test;

public class GameRankerTest {
	TeamHelper teamHelper = new TeamHelper(new TestTeamProvider());

	@Test
	public void rankPlayers() {
		GameRanker gameRanker = new GameRanker(teamHelper);
		gameRanker.rank(TestTeamProvider.game1);
		gameRanker.rank(TestTeamProvider.game2);
		print(gameRanker);
	}

	private void print(GameRanker gameRanker) {
		SortedMap<String, Double> teams = gameRanker.getRanks();
		for (Entry<String, Double> team : teams.entrySet()) {
			System.out.println(team.getKey()+": "+team.getValue().intValue());
		}
	}

}
