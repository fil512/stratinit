package com.kenstevens.stratinit.rank;

import java.util.Collection;
import java.util.List;

import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.model.Nation;

public interface TeamProvider {

	Collection<Nation> getAllies(Nation nation);

	List<Nation> getNations(Game game);

	int getNumberOfCities(Nation nation);

	List<SITeam> findTeams(GameHistory game);

	void getTeamsAndNations(GameHistory gameHistory, List<SITeam> teams,
			List<SINation> nations);

}
