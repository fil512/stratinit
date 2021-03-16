package com.kenstevens.stratinit;

import com.kenstevens.stratinit.client.model.Account;
import com.kenstevens.stratinit.client.rest.StratInitRestClient;
import com.kenstevens.stratinit.client.rest.StratInitServerClient;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.Constants;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientServerTest extends ClientServerBase {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // FIXME these names are too similar
    @Autowired
    private StratInitServerClient stratInitServerClient;
    @Autowired
    private StratInitRestClient stratInitRestClient;
    @Autowired
    private Account account;

    @Test
    public void testGoodLogin() throws IOException {
        goodLogin();

        Result<String> result = stratInitServerClient.getVersion();
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
        stratInitRestClient.setAccount();
    }

    // FIXME for some reason two transactions in a row fails.  Need to store a cookie in our rest client?
    @Disabled
    @Test
    public void testGetMyGames() throws IOException {
        goodLogin();
        Result<List<SIGame>> result = stratInitServerClient.getJoinedGames();
        assertResult(result);
        List<SIGame> games = result.getValue();

        for (SIGame game : games) {
            logger.info("Game #" + game.id + ": " + game.name);
        }
    }

    // FIXME find ways to add more methods

}
