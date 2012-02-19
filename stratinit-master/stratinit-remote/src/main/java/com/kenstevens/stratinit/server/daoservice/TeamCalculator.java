package com.kenstevens.stratinit.server.daoservice;

import java.util.List;

import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.Game;

public interface TeamCalculator {

	List<SITeam> getTeams(Game game);

}
