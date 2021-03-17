package com.kenstevens.stratinit;

import com.kenstevens.stratinit.client.event.EventScheduler;
import com.kenstevens.stratinit.client.model.Account;
import com.kenstevens.stratinit.client.rest.RestClient;
import com.kenstevens.stratinit.client.rest.StratInitServerClient;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientServerTest extends ClientServerBase {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StratInitServerClient stratInitServerClient;
    @Autowired
    private RestClient restClient;
    @Autowired
    private Account account;
    @Autowired
    private EventScheduler eventScheduler;
    private boolean initialized;

    @BeforeEach
    public void login() throws IOException {
        if (!initialized) {
            eventScheduler.updateGamesAndStartTimer();
            goodLogin();
        }
        initialized = true;
    }

    @Test
    public void testGoodLogin() throws IOException {

        Result<String> result = stratInitServerClient.getVersion();
        assertResult(result);
        String reply = result.getValue();
        assertEquals(Constants.SERVER_VERSION, reply);
    }

    @Test
    public void testTwoRequests() throws IOException {
        Result<String> result = stratInitServerClient.getVersion();
        assertResult(result);
        result = stratInitServerClient.getVersion();
        assertResult(result);
        String reply = result.getValue();
        assertEquals(Constants.SERVER_VERSION, reply);
    }

    private void goodLogin() throws IOException {
        setPlayer("test1");
    }

    private void setPlayer(String username) {
        account.setUsername(username);
        account.setPassword("testy");
        restClient.setAccount();
    }

    // FIXME get more than one of these to pass.  need to store cookies?
    @Test
    public void testGetMyGames() throws IOException {
        Result<List<SIGame>> result = stratInitServerClient.getJoinedGames();
        assertResult(result);
        List<SIGame> games = result.getValue();

        for (SIGame game : games) {
            logger.info("Game #" + game.id + ": " + game.name);
        }
    }

    // FIXME find ways to add more methods
    protected void assertResult(Result<? extends Object> result) {
        assertTrue(result.isSuccess(), result.toString());
    }
}
