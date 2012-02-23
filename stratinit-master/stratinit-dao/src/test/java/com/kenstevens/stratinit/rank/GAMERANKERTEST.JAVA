package com.kenstevens.stratinit.rank;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.kenstevens.stratinit.model.PlayerRank;

public class GameRankerTest {
	TeamHelper teamHelper = new TeamHelper(new TestTeamProvider());

	@Test
	public void rankPlayers() {
		GameRanker gameRanker = new GameRanker(teamHelper);
		gameRanker.rank(TestTeamProvider.game1);
		gameRanker.rank(TestTeamProvider.game2);
		List<PlayerRank> teams = gameRanker.getRanks();
		assertEquals(4, teams.size());
		assertEquals("playerc", teams.get(0).getName());
		assertEquals("playere playerf", teams.get(3).getName());
	}
}
