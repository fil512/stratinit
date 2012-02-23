package com.kenstevens.stratinit.server.daoservice.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.rank.TeamHelper;
import com.kenstevens.stratinit.rank.TeamProvider;
import com.kenstevens.stratinit.server.daoservice.TeamCalculator;

@Service
public class TeamCalculatorImpl implements TeamCalculator {
	@Autowired
	private TeamProvider nationAllyProvider;

	private static final Comparator<SITeam> BY_SCORE = new Comparator<SITeam>() {
		public int compare(SITeam p1, SITeam p2) {
			return Integer.valueOf(p2.score)
			.compareTo(p1.score);
		}
	};;

	@Override
	public List<SITeam> getTeams(Game game) {
		TeamHelper teamHelper = new TeamHelper(nationAllyProvider);
		List<SITeam> retval = teamHelper.findTeams(game);
		Collections.sort(retval, BY_SCORE);
		return retval;
	}




}
