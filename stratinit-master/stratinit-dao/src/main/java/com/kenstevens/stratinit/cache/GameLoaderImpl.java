package com.kenstevens.stratinit.cache;

import com.kenstevens.stratinit.dal.*;
import com.kenstevens.stratinit.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameLoaderImpl implements GameLoader {
    private final Log logger = LogFactory.getLog(getClass());
    private final GameDal gameDal;
    private final NationDal nationDal;
    private final RelationDal relationDal;
    private final SectorDal sectorDal;
    private final UnitDal unitDal;
    private final CityDal cityDal;
    // FIXME autowire

    public GameLoaderImpl(GameDal gameDal, NationDal nationDal, RelationDal relationDal, SectorDal sectorDal, UnitDal unitDal, CityDal citydal) {
        this.gameDal = gameDal;
        this.nationDal = nationDal;
        this.relationDal = relationDal;
        this.sectorDal = sectorDal;
        this.unitDal = unitDal;
        this.cityDal = citydal;
    }

    public GameCache loadGame(int gameId) {
        logger.info("Loading game #" + gameId + " into cache.");

        Optional<Game> oGame = gameDal.findById(gameId);
        if (!oGame.isPresent()) {
            return null;
        }
        Game game = oGame.get();
        logger.info("Getting Relations");
        List<Relation> relations = relationDal.findByGame(game);
        GameCache gameCache = new GameCache(game, relations);
        logger.info("Getting Nations");
        gameCache.addNations(nationDal.findByNationPKGame(game));
        if (game.isMapped()) {
            if (game.getSize() == 0) {
                logger.error("Cannot load game " + game + " because it is mapped but has no size.");
                return null;
            }
            logger.info("Getting World");
            gameCache.setWorld(sectorDal.getWorld(game));
            logger.info("Getting Cities");
            setCities(gameCache);
            logger.info("Getting Units");
            setUnits(gameCache);
            logger.info("Getting Sectors Seen");
            gameCache.setSectorsSeen(sectorDal.getSectorsSeen(gameCache
                    .getGame()));
            logger.info("Getting Units Seen");
            gameCache.setUnitsSeen(unitDal.getUnitsSeen(gameCache.getGame()));
            logger.info("Getting Launched Satellites");
            gameCache.setLaunchedSatellites(unitDal.getSatellites(gameCache
                    .getGame()));
            logger.info("Getting Units Move");
            List<UnitMove> badUnitMoves = gameCache.setUnitsMove(unitDal.getUnitsMove(gameCache.getGame()));
            clearBadUnitMoves(badUnitMoves);
            logger.info("Getting City Moves");

            List<CityMove> badCityMoves = gameCache.setCityMoves(sectorDal.getCityMoves(gameCache.getGame()));
            clearBadCityMoves(badCityMoves);
        }
        logger.info("Game #" + gameId + " loaded.");
        return gameCache;
    }

    private void clearBadUnitMoves(List<UnitMove> badUnitMoves) {
        for (UnitMove unitMove : badUnitMoves) {
            logger.warn("Removing bad unit move id#" + unitMove.getId());
            unitDal.remove(unitMove);
        }
    }

    private void clearBadCityMoves(List<CityMove> badCityMoves) {
        for (CityMove cityMove : badCityMoves) {
            logger.warn("Removing bad city move id#" + cityMove.getId());
            sectorDal.remove(cityMove);
        }
    }

    private void setCities(GameCache gameCache) {
        List<City> cities = cityDal.findByCityPKGame(gameCache.getGame());
        for (City city : cities) {
            gameCache.add(city);
        }
    }

    private void setUnits(GameCache gameCache) {
        List<Unit> units = unitDal.getUnits(gameCache.getGame());
        for (Unit unit : units) {
            gameCache.add(unit);
        }
    }

    public void flush(GameCache gameCache) {
        gameCache.flush(gameDal, relationDal, sectorDal);
        for (NationCache nationCache : gameCache.getNationCaches()) {
            nationCache.flush(gameCache.getGameId(), nationDal, sectorDal, unitDal);
        }
    }
}
