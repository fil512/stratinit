package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.model.GameHistoryNation;
import com.kenstevens.stratinit.model.GameHistoryTeam;


public interface GameHistoryDaoService {
    void save(GameHistory gameHistory);

    void save(GameHistoryTeam gameHistoryTeam);

    void save(GameHistoryNation gameHistoryNation);
}