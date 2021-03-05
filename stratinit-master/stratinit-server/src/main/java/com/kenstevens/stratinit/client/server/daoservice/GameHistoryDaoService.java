package com.kenstevens.stratinit.client.server.daoservice;

import com.kenstevens.stratinit.client.model.GameHistory;
import com.kenstevens.stratinit.client.model.GameHistoryNation;
import com.kenstevens.stratinit.client.model.GameHistoryTeam;
import com.kenstevens.stratinit.repo.GameHistoryNationRepo;
import com.kenstevens.stratinit.repo.GameHistoryRepo;
import com.kenstevens.stratinit.repo.GameHistoryTeamRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class GameHistoryDaoService {
    @Autowired
    private GameHistoryRepo gameHistoryRepo;
    @Autowired
    private GameHistoryTeamRepo gameHistoryTeamRepo;
    @Autowired
    private GameHistoryNationRepo gameHistoryNationRepo;

    public void save(GameHistory gameHistory) {
        gameHistoryRepo.save(gameHistory);
    }

    public void save(GameHistoryTeam gameHistoryTeam) {
        gameHistoryTeamRepo.save(gameHistoryTeam);
    }

    public void save(GameHistoryNation gameHistoryNation) {
        gameHistoryNationRepo.save(gameHistoryNation);
    }
}