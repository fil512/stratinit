package com.kenstevens.stratinit.rank;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.kenstevens.stratinit.dto.SITeamRank;

public class GameRankerTest {
	TeamHelper teamHelper = new TeamHelper(new TestTeamProvider());

	@Test
	public void rankTeams() {
		TeamRanker teamRanker = new TeamRanker(teamHelper);
		teamRanker.rank(TestTeamProvider.game1);
		teamRanker.rank(TestTeamProvider.game2);
		List<SITeamRank> teams = teamRanker.getTeamRanks();
		assertEquals(4, teams.size());
		assertEquals("playerc", teams.get(0).getName());
		assertEquals("playere playerf", teams.get(3).getName());
	}
}
