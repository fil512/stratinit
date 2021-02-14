package com.kenstevens.stratinit.rank;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.dto.SITeamRank;
import com.kenstevens.stratinit.model.PlayerRank;
import com.kenstevens.stratinit.server.remote.rank.ELOCalculator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TeamRanksTest {

	@Test
	public void testRankTeamsTieLost() {
		List<SITeam> teams = Lists.newArrayList();
		teams.add(new SITeam("a", "b", 11));
		teams.add(new SITeam("c", "d", 10));
		teams.add(new SITeam("e", "f", 10));
		TeamRanks teamRanks = new TeamRanks();
		teamRanks.rankTeams(teams);
		List<SITeamRank> ranks = teamRanks.getTeamRanks();
		assertEquals(3, ranks.size());
		assertEquals("a b", ranks.get(0).getName());
		assertEquals("c d", ranks.get(1).getName());
		assertEquals("e f", ranks.get(2).getName());
		assertEquals(1, ranks.get(0).getPlayed());
		assertEquals(1, ranks.get(1).getPlayed());
		assertEquals(1, ranks.get(2).getPlayed());
		assertEquals(2, ranks.get(0).getOpponents());
		assertEquals(1, ranks.get(1).getOpponents());
		assertEquals(1, ranks.get(2).getOpponents());
		assertEquals(1, ranks.get(0).getWins());
		assertEquals(0, ranks.get(1).getWins());
		assertEquals(0, ranks.get(2).getWins());
		assertEquals(2, ranks.get(0).getVictories());
		assertEquals(0, ranks.get(1).getVictories());
		assertEquals(0, ranks.get(2).getVictories());
		assertEquals(TeamRankMap.START_RANK + ELOCalculator.WEIGHT, ranks.get(0).getRank(), 1.0);
		assertEquals(TeamRankMap.START_RANK - ELOCalculator.WEIGHT / 2, ranks.get(1).getRank(), 1.0);
		assertEquals(TeamRankMap.START_RANK - ELOCalculator.WEIGHT / 2, ranks.get(2).getRank(), 1.0);
		List<PlayerRank> playerRanks = teamRanks.getPlayerRanks();
		assertEquals("a", playerRanks.get(0).getName());
		assertEquals("b", playerRanks.get(1).getName());
		assertEquals("c", playerRanks.get(2).getName());
		assertEquals("d", playerRanks.get(3).getName());
		assertEquals("e", playerRanks.get(4).getName());
		assertEquals("f", playerRanks.get(5).getName());
		assertEquals(TeamRankMap.START_RANK + ELOCalculator.WEIGHT, playerRanks.get(0).getRank(), 1.0);
		assertEquals(TeamRankMap.START_RANK + ELOCalculator.WEIGHT, playerRanks.get(1).getRank(), 1.0);
		assertEquals(0.0, playerRanks.get(2).getRank(), 1.0);
		assertEquals(0.0, playerRanks.get(3).getRank(), 1.0);
		assertEquals(0.0, playerRanks.get(4).getRank(), 1.0);
		assertEquals(0.0, playerRanks.get(5).getRank(), 1.0);
	}

	@Test
	public void testRankTeamsTieWon() {
		List<SITeam> teams = Lists.newArrayList();
		teams.add(new SITeam("a", "b", 11));
		teams.add(new SITeam("c", "d", 11));
		teams.add(new SITeam("e", "f", 10));
		TeamRanks teamRanks = new TeamRanks();
		teamRanks.rankTeams(teams);
		List<SITeamRank> ranks = teamRanks.getTeamRanks();
		assertEquals(3, ranks.size());
		assertEquals("a b", ranks.get(0).getName());
		assertEquals("c d", ranks.get(1).getName());
		assertEquals("e f", ranks.get(2).getName());
		assertEquals(1, ranks.get(0).getPlayed());
		assertEquals(1, ranks.get(1).getPlayed());
		assertEquals(1, ranks.get(2).getPlayed());
		assertEquals(1, ranks.get(0).getOpponents());
		assertEquals(1, ranks.get(1).getOpponents());
		assertEquals(2, ranks.get(2).getOpponents());
		assertEquals(1, ranks.get(0).getWins());
		assertEquals(1, ranks.get(1).getWins());
		assertEquals(0, ranks.get(2).getWins());
		assertEquals(1, ranks.get(0).getVictories());
		assertEquals(1, ranks.get(1).getVictories());
		assertEquals(0, ranks.get(2).getVictories());
		assertEquals(TeamRankMap.START_RANK + ELOCalculator.WEIGHT / 2, ranks.get(0).getRank(), 1.0);
		assertEquals(TeamRankMap.START_RANK + ELOCalculator.WEIGHT / 2, ranks.get(1).getRank(), 1.0);
		assertEquals(TeamRankMap.START_RANK - ELOCalculator.WEIGHT, ranks.get(2).getRank(), 1.0);
		List<PlayerRank> playerRanks = teamRanks.getPlayerRanks();
		assertEquals("a", playerRanks.get(0).getName());
		assertEquals("b", playerRanks.get(1).getName());
		assertEquals("c", playerRanks.get(2).getName());
		assertEquals("d", playerRanks.get(3).getName());
		assertEquals("e", playerRanks.get(4).getName());
		assertEquals("f", playerRanks.get(5).getName());
		assertEquals(TeamRankMap.START_RANK + ELOCalculator.WEIGHT / 2, playerRanks.get(0).getRank(), 1.0);
		assertEquals(TeamRankMap.START_RANK + ELOCalculator.WEIGHT / 2, playerRanks.get(1).getRank(), 1.0);
		assertEquals(TeamRankMap.START_RANK + ELOCalculator.WEIGHT / 2, playerRanks.get(2).getRank(), 1.0);
		assertEquals(TeamRankMap.START_RANK + ELOCalculator.WEIGHT / 2, playerRanks.get(3).getRank(), 1.0);
		assertEquals(0.0, playerRanks.get(4).getRank(), 1.0);
		assertEquals(0.0, playerRanks.get(5).getRank(), 1.0);
	}
}
