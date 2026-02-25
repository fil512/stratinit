package com.kenstevens.stratinit.server.rest.event;

import com.kenstevens.stratinit.client.model.GameHistory;
import com.kenstevens.stratinit.client.model.GameHistoryNation;
import com.kenstevens.stratinit.client.model.GameHistoryTeam;
import com.kenstevens.stratinit.helper.PlayerHelper;
import com.kenstevens.stratinit.repo.GameHistoryNationRepo;
import com.kenstevens.stratinit.repo.GameHistoryRepo;
import com.kenstevens.stratinit.repo.GameHistoryTeamRepo;
import com.kenstevens.stratinit.server.event.svc.GameArchiver;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GameArchiverTest extends TwoPlayerBase {
    @Autowired
    GameArchiver gameArchiver;
    @Autowired
    GameHistoryRepo gameHistoryRepo;
    @Autowired
    GameHistoryTeamRepo gameHistoryTeamRepo;
    @Autowired
    GameHistoryNationRepo gameHistoryNationRepo;

    @Test
    public void gameHistoryUpdated() {
        gameArchiver.archive(testGame);
        GameHistory gameHistory = gameHistoryRepo.findByGameId(testGame.getId()).get(0);
        assertNotNull(gameHistory);
        assertEquals(testGame.getStartTime(), gameHistory.getStartTime());
        assertEquals(testGame.getDuration(), gameHistory.getDuration());
        assertEquals(testGame.getEnds(), gameHistory.getEnds());
        assertEquals(testGame.getId().intValue(), gameHistory.getGameId());
        assertEquals(testGame.isBlitz(), gameHistory.isBlitz());
        assertEquals(testGame.getGamename(), gameHistory.getGamename());
    }

    @Test
    public void gameHistoryTeamUpdated() {
        gameArchiver.archive(testGame);
        GameHistory gameHistory = gameHistoryRepo.findByGameId(testGame.getId()).get(0);
        List<GameHistoryTeam> gameHistoryTeams = gameHistoryTeamRepo.findByGameHistory(gameHistory);
        assertEquals(2, gameHistoryTeams.size());
    }

    @Test
    public void gameHistoryNationUpdated() {
        gameArchiver.archive(testGame);
        GameHistory gameHistory = gameHistoryRepo.findByGameId(testGame.getId()).get(0);
        List<GameHistoryTeam> gameHistoryTeams = gameHistoryTeamRepo.findByGameHistory(gameHistory);
        int i = 0;
        for (GameHistoryTeam gameHistoryTeam : gameHistoryTeams) {
            List<GameHistoryNation> nations = gameHistoryNationRepo.findByGameHistoryTeam(gameHistoryTeam);
            assertEquals(1, nations.size());
            GameHistoryNation nation = nations.get(0);
            if (i++ == 0) {
                assertEquals(PlayerHelper.PLAYER_ME, nation.getGamename());
            } else {
                assertEquals(PLAYER_THEM_NAME, nation.getGamename());
            }
            assertEquals(2, nation.getCities());
            assertEquals(3, nation.getPower());
        }
    }

}
