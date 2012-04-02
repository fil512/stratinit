package com.kenstevens.stratinit.rank;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.kenstevens.stratinit.dto.SITeam;

public class TeamHelperTest {
	TestTeamProvider tp = new TestTeamProvider();
	TeamHelper teamHelper = new TeamHelper(tp);

	@Test
	public void testGame1() {
		List<SITeam> teams = teamHelper.findTeams(TestTeamProvider.game1);
		assertEquals(2, teams.size());
		SITeam team1 = teams.get(0);
		assertEquals(25, team1.score);
		assertEquals(TestTeamProvider.nation1a.getName(), team1.nation1);
		assertEquals(TestTeamProvider.nation1b.getName(), team1.nation2);
		SITeam team2 = teams.get(1);
		assertEquals(20, team2.score);
		assertEquals(TestTeamProvider.nation1c.getName(), team2.nation1);
		assertEquals(TestTeamProvider.nation1d.getName(), team2.nation2);
	}

	@Test
	public void testGame2() {
		List<SITeam> teams = teamHelper.findTeams(TestTeamProvider.game2);
		assertEquals(3, teams.size());
		SITeam team1 = teams.get(0);
		assertEquals(50, team1.score);
		assertEquals(TestTeamProvider.nation2c.getName(), team1.nation1);
		assertEquals(null, team1.nation2);
		SITeam team2 = teams.get(1);
		assertEquals(TestTeamProvider.nation2a.getName(), team2.nation1);
		assertEquals(TestTeamProvider.nation2b.getName(), team2.nation2);
		assertEquals(20, team2.score);
		SITeam team3 = teams.get(2);
		assertEquals(20, team3.score);
		assertEquals(TestTeamProvider.nation2e.getName(), team3.nation1);
		assertEquals(TestTeamProvider.nation2f.getName(), team3.nation2);
	}
}
