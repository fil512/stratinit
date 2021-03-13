package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.repo.NationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class NationDao extends CacheDao {
    @Autowired
    private NationRepo nationRepo;

    public Nation findNation(Game game, Player player) {
        return findNation(game.getId(), player);
    }

    public Nation findNation(int gameId, Player player) {
        GameCache gameCache = getGameCache(gameId);
        if (gameCache == null) {
            return null;
        }
        return gameCache.getNation(player);
    }

    public Nation getNation(int gameId, int nationId) {
        Collection<Nation> nations = getGameCache(gameId).getNations();
        for (Nation nation : nations) {
            if (nation.getNationId() == nationId) {
                return nation;
            }
        }
        return null;
    }

    public List<Nation> getNations(Game game) {
        return getGameCache(game).getNations();
    }

    public List<Nation> getNations(Player player) {
        List<GameCache> gameCaches = dataCache.getGameCaches();
        List<Nation> retval = new ArrayList<Nation>();
        for (GameCache gameCache : gameCaches) {
            Nation nation = gameCache.getNation(player);
            if (nation != null) {
                retval.add(nation);
            }
        }
        return retval;
    }

    public void markCacheModified(Nation nation) {
        getNationCache(nation).setModified(true);
    }

    public void save(Nation nation) {
        nationRepo.save(nation);
        getGameCache(nation.getGameId()).add(nation);
    }

}
