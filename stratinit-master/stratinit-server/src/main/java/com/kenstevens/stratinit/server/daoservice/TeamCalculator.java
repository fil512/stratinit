package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.Game;

import java.util.List;

public interface TeamCalculator {

	List<SITeam> getTeams(Game game);

}
