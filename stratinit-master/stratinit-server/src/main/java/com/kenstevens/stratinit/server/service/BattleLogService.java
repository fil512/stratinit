package com.kenstevens.stratinit.server.service;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.dao.LogDao;
import com.kenstevens.stratinit.dao.NationDao;
import com.kenstevens.stratinit.server.rest.svc.GameNotificationService;
import com.kenstevens.stratinit.type.GameEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BattleLogService {

    @Autowired
    private LogDao logDao;
    @Autowired
    private NationDao nationDao;
    @Autowired
    private GameNotificationService gameNotificationService;
    @Autowired
    private EventLogService eventLogService;

    public void save(BattleLog battleLog) {
        logDao.save(battleLog);
        Nation defender = battleLog.getDefender();
        if (defender != null && battleLog.getAttackType() == AttackType.INITIAL_ATTACK) {
            defender.setNewBattle(true);
            nationDao.markCacheModified(defender);
        }
        int gameId = battleLog.getAttacker().getGameId();
        String attackerName = battleLog.getAttacker().getName();
        String defenderName = defender != null ? defender.getName() : null;
        GameEventType eventType = (battleLog instanceof CityNukedBattleLog) ? GameEventType.NUKE
                : (battleLog instanceof CityCapturedBattleLog) ? GameEventType.CITY_CAPTURE
                : GameEventType.BATTLE;
        String desc = attackerName + " attacks" + (defenderName != null ? " " + defenderName : "") + " at " + battleLog.getCoords();
        eventLogService.logServerEvent(gameId, attackerName, eventType, desc, battleLog.getCoords());
        gameNotificationService.notifyBattle(gameId, battleLog.getCoords());
    }

    public void removeLogs(Game game) {
        logDao.removeLogs(game);
    }

    public int logError(Game game, Player player, String stackTrace) {
        ErrorLog errorLog = new ErrorLog(game == null ? -1 : game.getId(), player == null ? "NO_PLAYER" : player.getUsername(), stackTrace);
        logDao.save(errorLog);
        return errorLog.getId();
    }

}