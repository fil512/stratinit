package com.kenstevens.stratinit.rank;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.GameHistory;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.dto.SITeam;

import java.util.*;

public class TeamHelper {

	private static final Comparator<SITeam> BY_SCORE = new Comparator<SITeam>() {
		public int compare(SITeam p1, SITeam p2) {
			return Integer.valueOf(p2.score).compareTo(p1.score);
		}
	};
	private final ITeamProvider teamProvider;

	public List<SITeam> findTeams(Game game) {
		List<SITeam> retval = findUnsortedTeams(game);
		Collections.sort(retval, BY_SCORE);
		return retval;
	}

	public TeamHelper(ITeamProvider teamProvider) {
		this.teamProvider = teamProvider;
	}

	private List<SITeam> findUnsortedTeams(Game game) {
		List<Nation> nations = teamProvider.getNations(game);

		List<SITeam> teams = new ArrayList<SITeam>();
		for (Nation nation : nations) {
			if (contains(teams, nation)) {
				continue;
			}
			Collection<Nation> allies = teamProvider.getAllies(nation);
			int nationCityCount = teamProvider.getNumberOfCities(nation);
			SITeam siteam;
			if (allies.isEmpty()) {
				siteam = new SITeam(nation.getName(), null, nationCityCount);
			} else {
				// TODO REF generalize to support multiple players on a team
				Nation ally = allies.iterator().next();
				int allyCityCount = teamProvider.getNumberOfCities(ally);
				siteam = new SITeam(nation.getName(), ally.getName(),
						nationCityCount + allyCityCount);
			}
			teams.add(siteam);
		}
		return teams;
	}

	private boolean contains(List<SITeam> teams, Nation nation) {
		for (SITeam team : teams) {
			// TODO OPT switch from name to nationId
			if (nation.getName().equals(team.nation1)) {
				return true;
			}
			if (nation.getName().equals(team.nation2)) {
				return true;
			}
		}
		return false;
	}

	public List<SITeam> findTeams(GameHistory game) {
		return teamProvider.findTeams(game);
	}

}
