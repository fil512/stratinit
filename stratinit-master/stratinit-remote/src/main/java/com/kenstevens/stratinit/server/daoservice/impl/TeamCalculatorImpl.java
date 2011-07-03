package com.kenstevens.stratinit.server.daoservice.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.server.daoservice.TeamCalculator;

@Service
public class TeamCalculatorImpl implements TeamCalculator {
	@Autowired
	private GameDao gameDao;
	@Autowired
	private SectorDao sectorDao;

	private static final Comparator<SITeam> BY_SCORE = new Comparator<SITeam>() {
		public int compare(SITeam p1, SITeam p2) {
			return Integer.valueOf(p2.score)
			.compareTo(p1.score);
		}
	};;

	@Override
	public List<SITeam> getTeams(Game game) {
		List<SITeam> retval = findTeams(game);
		Collections.sort(retval, BY_SCORE);
		return retval;
	}

	private List<SITeam> findTeams(Game game) {
		List<SITeam> teams = new ArrayList<SITeam>();
		List<Nation> nations = gameDao.getNations(game);
		for (Nation nation : nations) {
			if (contains(teams, nation)) {
				continue;
			}
			Collection<Nation> allies = gameDao.getAllies(nation);
			int nationCityCount = sectorDao.getNumberOfCities(nation);
			SITeam siteam;
			if (allies.isEmpty()) {
				siteam = new SITeam(nation.getName(), null, nationCityCount);
			} else {
				// TODO REF generalize to support multiple players on a team
				Nation ally = allies.iterator().next();
				int allyCityCount = sectorDao.getNumberOfCities(ally);
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
