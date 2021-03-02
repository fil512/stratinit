package com.kenstevens.stratinit.server.event.svc;

import com.kenstevens.stratinit.model.CityPK;
import com.kenstevens.stratinit.model.RelationPK;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitSeenPK;
import com.kenstevens.stratinit.server.event.update.EventUpdateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class StratInitUpdater {
    @Autowired
    private EventUpdateFactory eventUpdateFactory;

    public void updateTech(Integer gameId, Date date) {
        eventUpdateFactory.getTechEventUpdate(date).update(gameId);
    }

    public void buildUnit(CityPK cityPK, Date date) {
        eventUpdateFactory.getBuildUnitEventUpdate(cityPK, date).update(cityPK.getGameId());
    }

    public void endGame(Integer gameId) {
        eventUpdateFactory.getEndGameEventUpdate().update(gameId);
    }

    public void startGame(Integer gameId) {
        eventUpdateFactory.getStartGameEventUpdate().update(gameId);
    }

    public void switchRelation(RelationPK relationPK) {
        eventUpdateFactory.getSwitchRelationEventUpdate(relationPK).update(relationPK.getGameId());
    }

    public void disable(UnitSeenPK unitSeenPK) {
        eventUpdateFactory.getDisableUnitSeenEventUpdate(unitSeenPK).update(unitSeenPK.getGameId());
    }

    public void updateUnit(Unit unit, Date date) {
        eventUpdateFactory.getUpdateUnitEventUpdate(unit, date).update(unit.getGameId());
    }

    public void mapGame(Integer gameId) {
        eventUpdateFactory.getMapGameEventUpdate().update(gameId);
    }
}
