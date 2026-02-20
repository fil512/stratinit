package com.kenstevens.stratinit.cache;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GameLoader {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    CacheFactory cacheFactory;
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
        logger.debug("Loading game #" + gameId + " into cache.");

        Optional<Game> oGame = gameRepo.findById(gameId);
        if (!oGame.isPresent()) {
            return null;
        }
        Game game = oGame.get();
        logger.debug("Getting Relations");
        Iterable<Relation> relations = relationRepo.findAll(QRelation.relation.relationPK.from.nationPK.game.eq(game));
        List<Relation> relationList = new ArrayList<>();
        relations.forEach(relationList::add);
        GameCache gameCache = cacheFactory.newGameCache(game, relationList);
        logger.debug("Getting Nations");
        gameCache.addNations(nationRepo.findAll(QNation.nation.nationPK.game.eq(game)));
        if (game.isMapped()) {
            if (game.getGamesize() == 0) {
                logger.error("Cannot load game " + game + " because it is mapped but has no size.");
                return null;
            }
            logger.info("Getting World");
            gameCache.setWorld(getWorld(game));
            loadGameCacheFromDatabase(gameCache);
        }
        logger.info("Game #{} {} loaded into cache", game.getId(), game.getGamename());
        return gameCache;
    }

    private void loadGameCacheFromDatabase(GameCache gameCache) {
        logger.debug("Getting Cities");
        setCities(gameCache);
        logger.debug("Getting Units");
        setUnits(gameCache);

        logger.debug("Getting Sectors Seen");
        Game game = gameCache.getGame();
        gameCache.setSectorsSeen(sectorSeenRepo.findAll(QSectorSeen.sectorSeen.sectorSeenPK.nation.nationPK.game.eq(game)));
        logger.debug("Getting Units Seen");
        gameCache.setUnitsSeen(unitSeenRepo.findAll(QUnitSeen.unitSeen.unitSeenPK.nation.nationPK.game.eq(game)));
        logger.debug("Getting Launched Satellites");
        gameCache.setLaunchedSatellites(launchedSatelliteRepo.findAll(QLaunchedSatellite.launchedSatellite.nation.nationPK.game.eq(game)));
        logger.debug("Getting Units Move");
        List<UnitMove> badUnitMoves = gameCache.setUnitsMove(unitMoveRepo.findAll(QUnitMove.unitMove.unit.nation.nationPK.game.eq(game)));
        clearBadUnitMoves(badUnitMoves);
        logger.debug("Getting City Moves");

        List<CityMove> badCityMoves = gameCache.setCityMoves(cityMoveRepo.findAll(QCityMove.cityMove.city.nation.nationPK.game.eq(game)));
        clearBadCityMoves(badCityMoves);
    }

    private World getWorld(Game game) {
        World world = new World(game, false);
        sectorRepo.findAll(QSector.sector.sectorPK.game.eq(game)).forEach(world::setSector);
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
        cityRepo.findAll(QCity.city.cityPK.game.eq(gameCache.getGame())).forEach(gameCache::add);
    }

    private void setUnits(GameCache gameCache) {
        unitRepo.findAll(QUnit.unit.nation.nationPK.game.eq(gameCache.getGame())).forEach(gameCache::add);
    }

    public void flush(GameCache gameCache) {
        gameCache.flush(gameRepo, relationRepo, sectorRepo);
        for (NationCache nationCache : gameCache.getNationCaches()) {
            nationCache.flush(gameCache.getGameId());
        }
    }
}
