package com.kenstevens.stratinit.server.daoserviceimpl;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.server.daoservice.IntegrityCheckerService;
import com.kenstevens.stratinit.server.daoservice.MessageDaoService;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.remote.ServerManager;
import com.kenstevens.stratinit.type.SectorCoords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class IntegrityCheckerServiceImpl implements IntegrityCheckerService {
    private static final int MAX_ALLOWABLE_DEPTH = 1024;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UnitDao unitDao;
    @Autowired
    SectorDaoService sectorDaoService;
    @Autowired
    UnitDaoService unitDaoService;
    @Autowired
    MessageDaoService messageDaoService;
    @Autowired
    GameDao gameDao;
    @Autowired
    ServerManager serverManager;

    @Override
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
        Multimap<SectorCoords, Unit> unitsByCoords = Multimaps.index(units,
                new Function<Unit, SectorCoords>() {
                    @Override
                    public SectorCoords apply(Unit unit) {
                        return unit.getCoords();
                    }
                });
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
        WorldSector worldSector = sectorDaoService.getAllWorldView(firstNation).getWorldSector(firstUnit);
        Nation worldSectorNation = worldSector.getNation();
        Collection<Nation> allies = gameDao.getAllies(firstNation);
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
        WorldView worldView = sectorDaoService.getAllWorldView(nation);
        List<WorldSector> neighbours = worldView.getNeighbours(unit.getCoords());
        for (WorldSector neighbour : neighbours) {
            if (neighbour.canEnter(unit)) {
                logger.warn("Moving " + unit.toEnemyString() + " to " + neighbour.getCoords());
                messageDaoService.notify(nation, unit + " moved", message + "\n" + unit + " in " + unit.getCoords() + " moved to " + neighbour.getCoords());
                unit.setCoords(neighbour.getCoords());
                unitDaoService.merge(unit);
                return;
            }
        }
        logger.warn("Removing " + unit.toEnemyString());
        messageDaoService.notify(nation, unit + " removed", message + "\n" + unit + " in " + unit.getCoords() + " Removing " + unit + " in " + unit.getCoords() + ".");
        unitDaoService.killUnit(unit);
    }

}
