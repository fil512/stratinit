package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.cache.GameLoader;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.client.util.GameNameFile;
import com.kenstevens.stratinit.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    private SectorRepo sectorRepo;
    @Autowired
    private UnitRepo unitRepo;

    public Game findGame(int gameId) {
        GameCache gameCache = getGameCache(gameId);
        if (gameCache == null) {
            return null;
        }
        return gameCache.getGame();
    }

    public void flush() {
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
            gameRepo.save(game);
            dataCache.remove(game);
        }
    }

    @Transactional
    public void save(Game game) {
        game.setCreated();
        if (game.getGamename() == null) {
            game.setGamename("");
        }
        gameRepo.save(game);
        getGameCache(game.getId());
        if (game.getGamename().isEmpty()) {
            game.setGamename(GameNameFile.getName(game.getId()));
        }
    }

    @Transactional
    public void remove(Game game) {
        nationRepo.deleteAll(nationRepo.findAll(QNation.nation.nationPK.game.eq(game)));
        unitRepo.deleteAll(unitRepo.findAll(QUnit.unit.nation.nationPK.game.eq(game)));
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
        cityRepo.delete(city);
    }


}
