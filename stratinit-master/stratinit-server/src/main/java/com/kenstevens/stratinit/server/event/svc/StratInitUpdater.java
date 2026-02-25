package com.kenstevens.stratinit.server.event.svc;

import com.kenstevens.stratinit.client.model.CityPK;
import com.kenstevens.stratinit.client.model.RelationPK;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.UnitSeenPK;
import com.kenstevens.stratinit.server.event.update.EventUpdateFactory;
import com.kenstevens.stratinit.server.rest.svc.GameNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class StratInitUpdater {
    @Autowired
    private EventUpdateFactory eventUpdateFactory;
    @Autowired
    private GameNotificationService gameNotificationService;

    public void updateTech(Integer gameId, Date date) {
        eventUpdateFactory.getTechEventUpdate(date).update(gameId);
        gameNotificationService.notifyGameUpdate(gameId, -1);
    }

    public void buildUnit(CityPK cityPK, Date date) {
        eventUpdateFactory.getBuildUnitEventUpdate(cityPK, date).update(cityPK.getGameId());
        gameNotificationService.notifyGameUpdate(cityPK.getGameId(), -1);
    }

    public void endGame(Integer gameId) {
        eventUpdateFactory.getEndGameEventUpdate().update(gameId);
    }

    public void startGame(Integer gameId) {
        eventUpdateFactory.getStartGameEventUpdate().update(gameId);
    }

    public void switchRelation(RelationPK relationPK) {
        eventUpdateFactory.getSwitchRelationEventUpdate(relationPK).update(relationPK.getGameId());
        gameNotificationService.notifyGameUpdate(relationPK.getGameId(), -1);
    }

    public void disable(UnitSeenPK unitSeenPK) {
        eventUpdateFactory.getDisableUnitSeenEventUpdate(unitSeenPK).update(unitSeenPK.getGameId());
    }

    public void updateUnit(Unit unit, Date date) {
        eventUpdateFactory.getUpdateUnitEventUpdate(unit, date).update(unit.getGameId());
        gameNotificationService.notifyGameUpdate(unit.getGameId(), -1);
    }

    public void mapGame(Integer gameId) {
        eventUpdateFactory.getMapGameEventUpdate().update(gameId);
    }

    public void executeBotTurns(Integer gameId) {
        eventUpdateFactory.getBotTurnEventUpdate().update(gameId);
        gameNotificationService.notifyGameUpdate(gameId, -1);
    }
}
