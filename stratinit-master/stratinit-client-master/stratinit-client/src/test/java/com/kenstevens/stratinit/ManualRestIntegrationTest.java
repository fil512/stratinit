package com.kenstevens.stratinit;

import com.kenstevens.stratinit.client.model.Account;
import com.kenstevens.stratinit.client.rest.RestStratInitClient;
import com.kenstevens.stratinit.client.rest.StratInitRestClient;
import com.kenstevens.stratinit.dto.SICityUpdate;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.SetGameJson;
import com.kenstevens.stratinit.type.Constants;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteAccessException;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Disabled
public class ManualRestIntegrationTest extends BaseStratInitClientTest {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RestStratInitClient stratInitServer;
	@Autowired
	private Account account;
	@Autowired
	private StratInitRestClient stratInitRestClient;

	@Test
	public void testGoodLogin() throws IOException {
		goodLogin();

		Result<String> result = stratInitServer.getVersion();
		assertResult(result);
		String reply = result.getValue();
		assertEquals(Constants.SERVER_VERSION, reply);
	}

	private void goodLogin() throws IOException {
		setPlayer("test1");
	}

	@Test
	public void testGetMyGames() throws IOException {
		goodLogin();
		Result<List<SIGame>> result = stratInitServer.getJoinedGames();
		assertResult(result);
		List<SIGame> games = result.getValue();

		for (SIGame game : games) {
			logger.info("Game #" + game.id + ": " + game.name);
		}
	}

	@Test
	public void testPlayersJoinGames() {
		setPlayer("test1");
		Result<List<SIGame>> games = stratInitServer.getUnjoinedGames();
		SIGame game = games.getValue().get(0);
		SetGameJson request = new SetGameJson(game.id, false);
		Result<SINation> result = stratInitServer.joinGame(request);
		assertResult(result);

		setPlayer("test2");
		result = stratInitServer.joinGame(request);
		assertResult(result);

		setPlayer("test3");
		result = stratInitServer.joinGame(request);
		assertResult(result);

		setPlayer("test4");
		result = stratInitServer.joinGame(request);
		assertResult(result);
	}

	private void setPlayer(String test1) {
		account.setUsername(test1);
		account.setPassword("testy");
		stratInitRestClient.setAccount();
	}

	@Test
	public void testGetUnjoinedGames() throws IOException {
		goodLogin();
		Result<List<SIGame>> result = stratInitServer.getUnjoinedGames();
		assertResult(result);
		List<SIGame> games = result.getValue();

		for (SIGame game : games) {
			logger.info("Game #" + game.id + ": " + game.name);
		}
	}

	@Test
	public void testGetSectors() throws IOException {
        goodLogin();
        Result<List<SISector>> result = stratInitServer.getSectors();
        assertResult(result);
        List<SISector> sectors = result.getValue();
        logger.info("GOT " + sectors.size() + " sectors!");
    }

	@Test
	public void testGetCities() throws IOException {
        goodLogin();
        List<SICityUpdate> cities = stratInitServer.getCities().getValue();
        logger.info("cities: " + cities.size());
    }

	@Test
	public void testBadLogin() {
		account.setUsername("hydro");
		account.setPassword("hydrox");
		try {
			String reply = stratInitServer.getVersion().getValue();
			fail();
		} catch (RemoteAccessException e) {
			assertThat(e.getMessage(), startsWith("Cannot access HTTP invoker remote service at"));
			assertThat(e.getMessage(), containsString("status code = " + HttpStatus.SC_UNAUTHORIZED));
		}
	}
}
