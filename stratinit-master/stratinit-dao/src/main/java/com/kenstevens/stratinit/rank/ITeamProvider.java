package com.kenstevens.stratinit.rank;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.GameHistory;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.dto.SITeam;

import java.util.Collection;
import java.util.List;

public interface ITeamProvider {
    Collection<Nation> getAllies(Nation nation);

    List<Nation> getNations(Game game);

    int getNumberOfCities(Nation nation);

    void getTeamsAndNations(GameHistory gameHistory, List<SITeam> teams, List<SINation> nations);

    List<SITeam> findTeams(GameHistory gameHistory);
}
