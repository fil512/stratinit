package com.kenstevens.stratinit.cache;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Relation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CacheFactory {
    @Autowired
    private ApplicationContext applicationContext;

    public GameCache newGameCache(Game game, List<Relation> relations) {
        return applicationContext.getBean(GameCache.class, game, relations);
    }

    public NationCache newNationCache(Nation nation) {
        return applicationContext.getBean(NationCache.class, nation);
    }

}
