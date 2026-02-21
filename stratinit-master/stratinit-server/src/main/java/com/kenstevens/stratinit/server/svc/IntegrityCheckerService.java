package com.kenstevens.stratinit.server.svc;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.server.service.MessageService;
import com.kenstevens.stratinit.server.service.RelationService;
import com.kenstevens.stratinit.server.service.SectorService;
import com.kenstevens.stratinit.server.service.UnitService;
import com.kenstevens.stratinit.server.rest.ServerManager;
import com.kenstevens.stratinit.type.SectorCoords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class IntegrityCheckerService {
    private static final int MAX_ALLOWABLE_DEPTH = 1024;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UnitDao unitDao;
    @Autowired
    SectorService sectorService;
    @Autowired
    UnitService unitService;
    @Autowired
    RelationService relationService;
    @Autowired
    MessageService messageService;
    @Autowired
    GameDao gameDao;
    @Autowired
    ServerManager serverManager;

    public void checkAndFix(Game game) {
        logger.info("Starting integrity check.");
        int depth = 0;
        while (checkUnits(game)) {
            ++depth;
            if (depth > MAX_ALLOWABLE_DEPTH) {
                logger.error("Too many stacked units.  Aborting.");
                serverManager.shutdown();
                throw new StackOverflowError("Too many stacked units.  Aborting.");
            }
        }
        logger.info("Integrity check complete:  " + depth + " errors");
    }

    private boolean checkUnits(Game game) {
        Collection<Unit> units = unitDao.getUnits(game);
        Multimap<SectorCoords, Unit> unitsByCoords = Multimaps.index(units, Unit::getCoords);
        for (SectorCoords coords : unitsByCoords.keySet()) {
            Collection<Unit> sectorUnits = unitsByCoords.get(coords);
            if (checkUnits(sectorUnits)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkUnits(
            Collection<Unit> units) {
        if (units.isEmpty()) {
            return false;
        }
        Unit firstUnit = units.iterator().next();
        Nation firstNation = firstUnit.getNation();
        WorldSector worldSector = sectorService.getAllWorldView(firstNation).getWorldSector(firstUnit);
        Nation worldSectorNation = worldSector.getNation();
        Collection<Nation> allies = relationService.getAllies(firstNation);
        if (worldSectorNation != null && !firstNation.equals(worldSectorNation) && !allies.contains(worldSectorNation)) {
            String message = badSectorMessage(firstUnit, worldSector);
            logger.warn(message);
            moveOrRemoveUnit(message, firstUnit);
            return true;
        }
        for (Unit unit : units) {
            Nation nation = unit.getNation();
            if (!firstNation.equals(nation) && !allies.contains(nation)) {
                String message = enemyUnitColoMessage(firstUnit, unit);
                logger.warn(message);
                moveOrRemoveUnit(message, unit);
                return true;
            }
        }
        return false;
    }

    private String badSectorMessage(Unit unit,
                                    WorldSector worldSector) {
        if (worldSector.isPlayerCity() || worldSector.getTopUnitType() == null) {
            return unit.toEnemyString() + " cannot exist in non-allied " + worldSector.getDescription() + " at " + worldSector.getCoords() + ".";
        } else {
            return unit.toEnemyString() + " cannot co-exist with non-allied " + worldSector.getDescription() + " at " + unit.getCoords() + ".";
        }
    }

    private String enemyUnitColoMessage(Unit firstUnit, Unit unit) {
        return unit.toEnemyString() + " cannot co-exist with non-allied " + firstUnit.toEnemyString() + " at " + unit.getCoords() + ".";
    }

    private void moveOrRemoveUnit(String message, Unit unit) {
        Nation nation = unit.getNation();
        WorldView worldView = sectorService.getAllWorldView(nation);
        List<WorldSector> neighbours = worldView.getNeighbours(unit.getCoords());
        for (WorldSector neighbour : neighbours) {
            if (neighbour.canEnter(unit)) {
                logger.warn("Moving " + unit.toEnemyString() + " to " + neighbour.getCoords());
                messageService.notify(nation, unit + " moved", message + "\n" + unit + " in " + unit.getCoords() + " moved to " + neighbour.getCoords());
                unit.setCoords(neighbour.getCoords());
                unitService.merge(unit);
                return;
            }
        }
        logger.warn("Removing " + unit.toEnemyString());
        messageService.notify(nation, unit + " removed", message + "\n" + unit + " in " + unit.getCoords() + " Removing " + unit + " in " + unit.getCoords() + ".");
        unitService.killUnit(unit);
    }

}
