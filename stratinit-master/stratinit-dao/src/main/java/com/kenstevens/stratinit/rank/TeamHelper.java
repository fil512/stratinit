package com.kenstevens.stratinit.rank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;

public class TeamHelper {

	private final TeamProvider teamProvider;

	public TeamHelper(TeamProvider teamProvider) {
		this.teamProvider = teamProvider;
	}
	
	public List<SITeam> findTeams(Game game) {
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
				siteam = new SITeam(nation.getName(), ally.getName(), nationCityCount+allyCityCount);
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

}
