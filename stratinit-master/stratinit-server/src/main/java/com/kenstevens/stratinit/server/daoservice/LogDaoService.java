package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.model.BattleLog;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Player;

public interface LogDaoService {

    void save(BattleLog battleLog);

    void removeLogs(Game game);

    int logError(Game game, Player player, String stackTrace);

}