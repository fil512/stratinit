package com.kenstevens.stratinit.cache;

import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.repo.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameLoaderImpl implements GameLoader {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private GameRepo gameRepo;
    @Autowired
    private NationRepo nationRepo;
    @Autowired
    private RelationRepo relationRepo;
    @Autowired
    private SectorRepo sectorRepo;
    @Autowired
    private CityRepo cityRepo;
    @Autowired
    private CityMoveRepo cityMoveRepo;
    @Autowired
    private SectorSeenRepo sectorSeenRepo;
    @Autowired
    private UnitSeenRepo unitSeenRepo;
    @Autowired
    private LaunchedSatelliteRepo launchedSatelliteRepo;
    @Autowired
    private UnitMoveRepo unitMoveRepo;
    @Autowired
    private UnitRepo unitRepo;

    public GameCache loadGame(int gameId) {
        logger.info("Loading game #" + gameId + " into cache.");

        Optional<Game> oGame = gameRepo.findById(gameId);
        if (!oGame.isPresent()) {
            return null;
        }
        Game game = oGame.get();
        logger.info("Getting Relations");
        List<Relation> relations = relationRepo.findByGame(game);
        GameCache gameCache = new GameCache(game, relations);
        logger.info("Getting Nations");
        gameCache.addNations(nationRepo.findByNationPKGame(game));
        if (game.isMapped()) {
            if (game.getSize() == 0) {
                logger.error("Cannot load game " + game + " because it is mapped but has no size.");
                return null;
            }
            logger.info("Getting World");
            gameCache.setWorld(getWorld(game));
            logger.info("Getting Cities");
            setCities(gameCache);
            logger.info("Getting Units");
            setUnits(gameCache);
            logger.info("Getting Sectors Seen");
            gameCache.setSectorsSeen(sectorSeenRepo.findByGame(gameCache
                    .getGame()));
            logger.info("Getting Units Seen");
            gameCache.setUnitsSeen(unitSeenRepo.findByGame(gameCache.getGame()));
            logger.info("Getting Launched Satellites");
            gameCache.setLaunchedSatellites(launchedSatelliteRepo.findByGame(gameCache.getGame()));
            logger.info("Getting Units Move");
            List<UnitMove> badUnitMoves = gameCache.setUnitsMove(unitMoveRepo.findByGame(gameCache.getGame()));
            clearBadUnitMoves(badUnitMoves);
            logger.info("Getting City Moves");

            List<CityMove> badCityMoves = gameCache.setCityMoves(cityMoveRepo.findByGame(gameCache.getGame()));
            clearBadCityMoves(badCityMoves);
        }
        logger.info("Game #" + gameId + " loaded.");
        return gameCache;
    }

    private World getWorld(Game game) {
        World world = new World(game, false);
        List<Sector> sectors = sectorRepo.findByGame(game);
        for (Sector sector : sectors) {
            world.setSector(sector);
        }
        return world;
    }

    private void clearBadUnitMoves(List<UnitMove> badUnitMoves) {
        for (UnitMove unitMove : badUnitMoves) {
            logger.warn("Removing bad unit move id#" + unitMove.getId());
            unitMoveRepo.delete(unitMove);
        }
    }

    private void clearBadCityMoves(List<CityMove> badCityMoves) {
        for (CityMove cityMove : badCityMoves) {
            logger.warn("Removing bad city move id#" + cityMove.getId());
            cityMoveRepo.delete(cityMove);
        }
    }

    private void setCities(GameCache gameCache) {
        List<City> cities = cityRepo.findByCityPKGame(gameCache.getGame());
        for (City city : cities) {
            gameCache.add(city);
        }
    }

    private void setUnits(GameCache gameCache) {
        List<Unit> units = unitRepo.findByGame(gameCache.getGame());
        for (Unit unit : units) {
            gameCache.add(unit);
        }
    }

    public void flush(GameCache gameCache) {
        gameCache.flush(gameRepo, relationRepo, sectorRepo);
        for (NationCache nationCache : gameCache.getNationCaches()) {
            nationCache.flush(gameCache.getGameId(), nationRepo, sectorRepo, cityRepo, sectorSeenRepo, unitRepo, unitSeenRepo, unitMoveRepo, launchedSatelliteRepo);
        }
    }
}
