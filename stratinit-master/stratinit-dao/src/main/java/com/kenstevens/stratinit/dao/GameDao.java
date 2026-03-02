package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.cache.CacheFactory;
import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.cache.GameLoader;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.client.util.GameNameFile;
import com.kenstevens.stratinit.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class GameDao extends CacheDao {
    @Autowired
    private GameRepo gameRepo;
    @Autowired
    private NationRepo nationRepo;
    @Autowired
    private CityRepo cityRepo;
    @Autowired
    private GameLoader gameLoader;
    @Autowired
    private CacheFactory cacheFactory;
    @Autowired
    private SectorRepo sectorRepo;
    @Autowired
    private UnitRepo unitRepo;
    @Autowired
    private UnitAttackedBattleLogRepo unitAttackedBattleLogRepo;
    @Autowired
    private CityCapturedBattleLogRepo cityCapturedBattleLogRepo;
    @Autowired
    private CityNukedBattleLogRepo cityNukedBattleLogRepo;
    @Autowired
    private FlakBattleLogRepo flakBattleLogRepo;
    @Autowired
    private UnitSeenRepo unitSeenRepo;
    @Autowired
    private UnitMoveRepo unitMoveRepo;
    @Autowired
    private CityMoveRepo cityMoveRepo;
    @Autowired
    private SectorSeenRepo sectorSeenRepo;
    @Autowired
    private RelationRepo relationRepo;
    @Autowired
    private LaunchedSatelliteRepo launchedSatelliteRepo;
    @Autowired
    private MailRepo mailRepo;

    public Game findGame(int gameId) {
        GameCache gameCache = getGameCache(gameId);
        if (gameCache == null) {
            return null;
        }
        return gameCache.getGame();
    }

    public void flush() {
        if (skipDb()) {
            return;
        }
        for (GameCache gameCache : dataCache.getGameCaches()) {
            gameLoader.flush(gameCache);
        }
    }

    public List<Game> getAllGames() {
        return dataCache.getAllGames();
    }

    public void merge(Game game) {
        if (game.isEnabled()) {
            getGameCache(game).setModified(true);
        } else {
            if (!skipDb()) {
                gameRepo.save(game);
            }
            dataCache.remove(game);
        }
    }

    @Transactional
    public void save(Game game) {
        game.setCreated();
        if (game.getGamename() == null) {
            game.setGamename("");
        }
        if (skipDb()) {
            game.setId(nextSyntheticId());
            GameCache gameCache = cacheFactory.newGameCache(game, new ArrayList<>());
            dataCache.putGameCache(gameCache);
        } else {
            gameRepo.save(game);
            getGameCache(game.getId());
        }
        if (game.getGamename().isEmpty()) {
            game.setGamename(GameNameFile.getName(game.getId()));
        }
    }

    @Transactional
    public void remove(Game game) {
        if (skipDb()) {
            dataCache.remove(game);
            return;
        }
        // Delete in FK dependency order: children before parents
        // Battle logs reference Unit and Nation
        unitAttackedBattleLogRepo.deleteAll(unitAttackedBattleLogRepo.findAll(
                QUnitAttackedBattleLog.unitAttackedBattleLog.attacker.nationPK.game.eq(game)));
        cityCapturedBattleLogRepo.deleteAll(cityCapturedBattleLogRepo.findAll(
                QCityCapturedBattleLog.cityCapturedBattleLog.attacker.nationPK.game.eq(game)));
        cityNukedBattleLogRepo.deleteAll(cityNukedBattleLogRepo.findAll(
                QCityNukedBattleLog.cityNukedBattleLog.attacker.nationPK.game.eq(game)));
        flakBattleLogRepo.deleteAll(flakBattleLogRepo.findAll(
                QFlakBattleLog.flakBattleLog.attacker.nationPK.game.eq(game)));
        // UnitSeen and UnitMove reference Unit
        unitSeenRepo.deleteAll(unitSeenRepo.findAll(
                QUnitSeen.unitSeen.unitSeenPK.nation.nationPK.game.eq(game)));
        unitMoveRepo.deleteAll(unitMoveRepo.findAll(
                QUnitMove.unitMove.unit.nation.nationPK.game.eq(game)));
        // CityMove references City
        cityMoveRepo.deleteAll(cityMoveRepo.findAll(
                QCityMove.cityMove.city.nation.nationPK.game.eq(game)));
        // Units and Cities reference Nation
        unitRepo.deleteAll(unitRepo.findAll(QUnit.unit.nation.nationPK.game.eq(game)));
        cityRepo.deleteAll(cityRepo.findAll(QCity.city.cityPK.game.eq(game)));
        // LaunchedSatellite, Relation, Mail reference Nation
        launchedSatelliteRepo.deleteAll(launchedSatelliteRepo.findAll(
                QLaunchedSatellite.launchedSatellite.nation.nationPK.game.eq(game)));
        relationRepo.deleteAll(relationRepo.findAll(
                QRelation.relation.relationPK.from.nationPK.game.eq(game)));
        mailRepo.deleteAll(mailRepo.findAll(QMail.mail.game.eq(game)));
        // SectorSeen references Nation and Sector
        sectorSeenRepo.deleteAll(sectorSeenRepo.findAll(
                QSectorSeen.sectorSeen.sectorSeenPK.nation.nationPK.game.eq(game)));
        // Nation and Sector reference Game
        nationRepo.deleteAll(nationRepo.findAll(QNation.nation.nationPK.game.eq(game)));
        sectorRepo.deleteAll(sectorRepo.findAll(QSector.sector.sectorPK.game.eq(game)));
        gameRepo.delete(game);
        dataCache.remove(game);
    }

    @Transactional
    public void removeGame(int id) {
        remove(getGameCache(id).getGame());
    }

    public List<Game> getUnjoinedGames(Player player) {
        List<Game> retval = new ArrayList<Game>();
        for (GameCache gameCache : dataCache.getGameCaches()) {
            if (gameCache.getNation(player) == null) {
                retval.add(gameCache.getGame());
            }
        }
        return retval;
    }

    public void remove(City city) {
        if (skipDb()) {
            return;
        }
        cityRepo.delete(city);
    }
}
