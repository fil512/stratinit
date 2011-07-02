package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.model.BattleLog;
import com.kenstevens.stratinit.model.Game;

public interface LogDaoService {

	public abstract void persist(BattleLog battleLog);

	public abstract void removeLogs(Game game);

}