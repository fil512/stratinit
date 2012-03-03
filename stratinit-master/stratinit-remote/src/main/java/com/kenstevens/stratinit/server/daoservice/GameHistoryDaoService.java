package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.model.GameHistoryNation;
import com.kenstevens.stratinit.model.GameHistoryTeam;


public interface GameHistoryDaoService {
	public abstract void persist(GameHistory gameHistory);
	public abstract void persist(GameHistoryTeam gameHistoryTeam);
	public abstract void persist(GameHistoryNation gameHistoryNation);
}