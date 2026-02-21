package com.kenstevens.stratinit.server.event.svc;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Relation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.dao.RelationDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.server.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class GameEnder {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CityDao cityDao;
    @Autowired
    private GameService gameService;
    @Autowired
    private UnitService unitService;
    @Autowired
    private RelationService relationService;
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private RelationDao relationDao;

    @Autowired
    private BattleLogService battleLogService;
    @Autowired
    private CityService cityService;
    @Autowired
    private GameArchiver gameArchiver;

    public void endGame(Game game) {
        logger.info("Ending game " + game);
        gameArchiver.archive(game);
        gameService.score(game);
        removeUnitsSeen(game);
        removeUnitMoves(game);
        removeRelations(game);
        removeLogs(game);
        removeUnits(game);
        removeCities(game);
        gameService.disable(game);
    }

    private void removeCities(Game game) {
        for (City city : new ArrayList<>(cityDao.getCities(game))) {
            cityService.remove(city);
        }
    }

    private void removeRelations(Game game) {
        for (Relation relation : new ArrayList<>(relationDao.getRelations(game))) {
            relationService.remove(relation);
        }
    }


    private void removeUnitsSeen(Game game) {
        unitService.removeUnitsSeen(game);
    }

    private void removeUnitMoves(Game game) {
        unitService.removeUnitMoves(game);
    }


    private void removeUnits(Game game) {
        for (Unit unit : new ArrayList<>(unitDao.getUnits(game))) {
            unitService.remove(unit);
        }
    }

    // TODO REF Push remove lists down to dao like this method
    private void removeLogs(Game game) {
        battleLogService.removeLogs(game);
    }
}
