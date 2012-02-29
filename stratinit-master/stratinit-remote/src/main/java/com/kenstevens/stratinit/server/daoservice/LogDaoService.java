package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.model.BattleLog;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Player;

public interface LogDaoService {

	public abstract void persist(BattleLog battleLog);

	public abstract void removeLogs(Game game);

	public abstract int logError(Game game, Player player, String stackTrace);

}