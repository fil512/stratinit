package com.kenstevens.stratinit.client.server.service;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.server.rest.StratInitDaoBase;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.service.GameService;
import com.kenstevens.stratinit.type.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameCPGainTest extends StratInitDaoBase {
    @Autowired
    private GameService gameService;

    @Test
    public void joinGameSetsInitialCPGain() {
        // When a player joins a game, CP gain should be set to the minimum rate
        Result<Nation> result = gameService.joinGame(playerMe, testGameId, false);
        assertTrue(result.isSuccess(), result.toString());

        Nation nation = result.getValue();
        int expectedMinHourlyCPGain = Constants.MIN_COMMAND_POINTS_GAINED_PER_TICK
                * 3600 / Constants.TECH_UPDATE_INTERVAL_SECONDS;
        assertTrue(nation.getHourlyCPGain() > 0,
                "CP gain should not be 0 after joining, expected at least " + expectedMinHourlyCPGain
                        + " but was " + nation.getHourlyCPGain());
        assertEquals(expectedMinHourlyCPGain, nation.getHourlyCPGain(),
                "CP gain should be set to minimum hourly rate on join");
    }
}
