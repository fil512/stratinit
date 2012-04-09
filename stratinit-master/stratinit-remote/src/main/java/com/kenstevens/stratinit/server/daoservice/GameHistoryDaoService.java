package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.model.GameHistoryNation;
import com.kenstevens.stratinit.model.GameHistoryTeam;


public interface GameHistoryDaoService {
	void persist(GameHistory gameHistory);
	void persist(GameHistoryTeam gameHistoryTeam);
	void persist(GameHistoryNation gameHistoryNation);
}