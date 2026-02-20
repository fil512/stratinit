package com.kenstevens.stratinit.server.rest.svc;

import com.kenstevens.stratinit.dto.SIBattleLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GameNotificationService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    public void notifyGameUpdate(int gameId, int actingNationId) {
        if (messagingTemplate == null) {
            return;
        }
        logger.debug("Sending game update for game {} from nation {}", gameId, actingNationId);
        messagingTemplate.convertAndSend("/topic/game/" + gameId,
                Map.of("type", "UPDATE", "gameId", gameId, "nationId", actingNationId));
    }

    public void notifyBattleLog(int gameId, List<SIBattleLog> logs) {
        if (messagingTemplate == null) {
            return;
        }
        logger.debug("Sending battle logs for game {}", gameId);
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/battles", logs);
    }
}
