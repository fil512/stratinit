package com.kenstevens.stratinit.server.bot;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.server.service.RelationService;
import com.kenstevens.stratinit.type.RelationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
public class BotWorldStateBuilder {
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private CityDao cityDao;
    @Autowired
    private GameDao gameDao;
    @Autowired
    private RelationService relationService;

    public BotWorldState build(Nation nation) {
        Game game = gameDao.findGame(nation.getGameId());
        Collection<Unit> allUnits = unitDao.getUnits(game);
        Map<Nation, RelationType> myRelations = relationService.getMyRelationsAsMap(nation);
        Map<Nation, RelationType> theirRelations = relationService.getTheirRelationTypesAsMap(nation);

        return new BotWorldState(
                nation,
                game,
                unitDao.getUnits(nation),
                cityDao.getCities(nation),
                allUnits,
                myRelations,
                theirRelations
        );
    }

    public BotWorldState build(Nation nation, long simulatedTimeMillis) {
        Game game = gameDao.findGame(nation.getGameId());
        Collection<Unit> allUnits = unitDao.getUnits(game);
        Map<Nation, RelationType> myRelations = relationService.getMyRelationsAsMap(nation);
        Map<Nation, RelationType> theirRelations = relationService.getTheirRelationTypesAsMap(nation);

        return new BotWorldState(
                nation,
                game,
                unitDao.getUnits(nation),
                cityDao.getCities(nation),
                allUnits,
                myRelations,
                theirRelations,
                simulatedTimeMillis
        );
    }
}
