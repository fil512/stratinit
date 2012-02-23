package com.kenstevens.stratinit.server.daoservice.impl;

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

	@Override
	public List<SITeam> getTeams(Game game) {
		TeamHelper teamHelper = new TeamHelper(nationAllyProvider);
		return teamHelper.findTeams(game);
	}




}
