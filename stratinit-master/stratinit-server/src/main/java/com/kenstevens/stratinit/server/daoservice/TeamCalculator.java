package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.rank.TeamHelper;
import com.kenstevens.stratinit.rank.TeamProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamCalculator {
	@Autowired
	private TeamProvider nationAllyProvider;

	public List<SITeam> getTeams(Game game) {
		TeamHelper teamHelper = new TeamHelper(nationAllyProvider);
		return teamHelper.findTeams(game);
	}
}

