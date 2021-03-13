package com.kenstevens.stratinit.server.event.svc;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.dao.NationDao;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.server.daoservice.GameHistoryDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.svc.TeamCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameArchiver {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GameHistoryDaoService gameHistoryDaoService;
    @Autowired
    private TeamCalculator teamCalculator;
    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private CityDao cityDao;
    @Autowired
    private UnitDaoService unitDaoService;
    @Autowired
    private NationDao nationDao;

    public void archive(Game game) {
        GameHistory gameHistory = new GameHistory(game);
        gameHistoryDaoService.save(gameHistory);
        List<SITeam> teams = teamCalculator.getTeams(game);
        for (SITeam team : teams) {
            GameHistoryTeam gameHistoryTeam = new GameHistoryTeam(gameHistory);
            gameHistoryDaoService.save(gameHistoryTeam);
            archiveTeam(gameHistoryTeam, game, team);
        }
    }


    private void archiveTeam(GameHistoryTeam gameHistoryTeam, Game game, SITeam team) {
        archiveNation(gameHistoryTeam, game, team.getNation1());
        if (team.getNation2() != null) {
            archiveNation(gameHistoryTeam, game, team.getNation2());
        }
    }

    private void archiveNation(GameHistoryTeam gameHistoryTeam, Game game,
                               String nationName) {
        Player player = playerDao.find(nationName);
        if (player == null) {
            logger.error("Unable to find player " + nationName + " for game " + game + ".  Not archiving team.");
            return;
        }
        Nation nation = nationDao.findNation(game, player);
        if (nation == null) {
            logger.error("Unable to find player " + player + " in game " + game + ".  Not archiving team.");
            return;
        }
        int cities = cityDao.getNumberOfCities(nation);
        int power = unitDaoService.getPower(nation);
        GameHistoryNation gameHistoryNation = new GameHistoryNation(gameHistoryTeam, nationName, cities, power);
        gameHistoryDaoService.save(gameHistoryNation);
    }
}
