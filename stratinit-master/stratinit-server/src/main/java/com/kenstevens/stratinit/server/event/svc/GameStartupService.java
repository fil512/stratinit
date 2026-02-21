package com.kenstevens.stratinit.server.event.svc;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.client.util.UpdateManager;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.dao.NationDao;
import com.kenstevens.stratinit.dao.RelationDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.svc.CityBuilderService;
import com.kenstevens.stratinit.server.svc.FogService;
import com.kenstevens.stratinit.server.svc.IntegrityCheckerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class GameStartupService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EventQueue eventQueue;
    @Autowired
    private CityDao cityDao;
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private NationDao nationDao;
    @Autowired
    private RelationDao relationDao;
    @Autowired
    private GameDaoService gameDaoService;
    @Autowired
    private UnitDaoService unitDaoService;
    @Autowired
    private FogService fogService;
    @Autowired
    private CityBuilderService cityBuilderService;
    @Autowired
    private IntegrityCheckerService integrityCheckerService;

    public void startGame(Game game, boolean fromEvent) {
        logger.info("Starting up game " + game + ".");
        integrityCheck(game);
        updateCities(game, fromEvent);
        updateUnits(game);
        updateRelations(game);
        updateUnitSeen(game);
        survey(game);
        gameDaoService.setNoAlliances(game);
        eventQueue.schedule(game, true);
    }

    private void integrityCheck(Game game) {
        integrityCheckerService.checkAndFix(game);
    }

    private void survey(Game game) {
        List<Nation> nations = nationDao.getNations(game);
        for (Nation nation : nations) {
            fogService.survey(nation);
        }
    }

    private void updateCities(Game game, boolean fromEvent) {
        Collection<City> cities = cityDao.getCities(game);
        if (cities.size() > 0) {
            logger.info("Updating " + cities.size() + " cities");
        }
        for (City city : cities) {
            if (fromEvent) {
                city.setLastUpdated(game.getStartTime());
            } else {
                UpdateManager updateManager = new UpdateManager(city);
                while (updateManager.missedUpdates() > 0) {
                    Date nextMissedBuildTime = updateManager
                            .getNextMissedBuildTime();
                    cityBuilderService.buildUnit(city, nextMissedBuildTime);
                }
            }
            eventQueue.schedule(city);
        }
        if (cities.size() > 0) {
            logger.info(cities.size() + " cities scheduled.");
        }
    }

    private void updateUnitSeen(Game game) {
        List<UnitSeen> unitsSeen = unitDao.getUnitsSeen(game);
        if (unitsSeen.size() > 0) {
            logger.info("Updating " + unitsSeen.size() + " units seen");
        }
        int disabledCount = 0;
        for (UnitSeen unitSeen : unitsSeen) {
            if (badUnitSeen(unitSeen)) {
                ++disabledCount;
                unitDaoService.disable(unitSeen);
                continue;
            }
            eventQueue.schedule(unitSeen);
        }
        if (disabledCount > 0) {
            logger.info(disabledCount + " units seen disabled.");
        }
        if (unitsSeen.size() > 0) {
            logger.info(unitsSeen.size() + " unit disappearances scheduled.");
        }
    }

    private boolean badUnitSeen(UnitSeen unitSeen) {
        Unit unit = unitSeen.getUnit();
        if (!unit.isAlive()) {
            return true;
        }
        return unit.getNation().equals(unitSeen.getNation());
    }

    private void updateRelations(Game game) {
        Collection<Relation> relations = relationDao.getAllChangingRelations(game);
        if (relations.size() > 0) {
            logger.info("Updating " + relations.size() + " relations");
        }
        for (Relation relation : relations) {
            eventQueue.schedule(relation);
        }
        if (relations.size() > 0) {
            logger.info(relations.size() + " relation changes scheduled.");
        }
    }

    private void updateUnits(Game game) {
        Collection<Unit> units = unitDao.getUnits(game);
        if (units.size() > 0) {
            logger.info("Updating " + units.size() + " units");
        }

        for (Unit unit : units) {
            UpdateManager updateManager = new UpdateManager(unit);
            while (updateManager.missedUpdates() > 0) {
                Date nextMissedBuildTime = updateManager
                        .getNextMissedBuildTime();
                unitDaoService.updateUnit(unit, nextMissedBuildTime);
            }
            eventQueue.schedule(unit);
        }
        if (units.size() > 0) {
            logger.info(units.size() + " units scheduled.");
        }
    }
}
