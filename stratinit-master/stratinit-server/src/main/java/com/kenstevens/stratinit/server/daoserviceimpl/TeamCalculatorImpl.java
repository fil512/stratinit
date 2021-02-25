package com.kenstevens.stratinit.server.daoserviceimpl;

import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.rank.TeamHelper;
import com.kenstevens.stratinit.rank.TeamProvider;
import com.kenstevens.stratinit.server.daoservice.TeamCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
