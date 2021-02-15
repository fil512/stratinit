package com.kenstevens.stratinit;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.model.Account;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.StratInit;
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
public class SpringRemotingIntegrationManualTest extends StratInitClientTest {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private StratInit stratInit;
	@Autowired
	private Account account;

	@Test
	public void testGoodLogin() throws IOException {
		goodLogin();

		Result<String> version = stratInit.getVersion();
		String reply = version.getValue();
		assertEquals(Constants.SERVER_VERSION, reply);
	}

	private void goodLogin() throws IOException {
		account.setUsername("test1");
		account.setPassword("testy");
	}

	@Test
	public void testGetMyGames() throws IOException {
		goodLogin();
		Result<List<SIGame>> joinedGames = stratInit.getJoinedGames();
		List<SIGame> games = joinedGames.getValue();

		for (SIGame game : games) {
			logger.info("Game #" + game.id + ": " + game.name);
		}
	}

	@Test
	public void testGetUnjoinedGames() throws IOException {
		goodLogin();
		Result<List<SIGame>> unJoinedGames = stratInit.getUnjoinedGames();
		List<SIGame> games = unJoinedGames.getValue();

		for (SIGame game : games) {
			logger.info("Game #" + game.id + ": " + game.name);
		}
	}

	@Test
	public void testGetSectors() throws IOException {
		goodLogin();
		List<SISector> sectors = stratInit.getSectors().getValue();
		logger.info("GOT " + sectors.size() + " sectors!");
	}

	@Test
	public void testGetCities() throws IOException {
		goodLogin();
		List<SICity> cities = stratInit.getCities().getValue();
		logger.info("cities: " + cities.size());
	}

	@Test
	public void testBadLogin() {
		account.setUsername("hydro");
		account.setPassword("hydrox");
		try {
			String reply = stratInit.getVersion().getValue();
			fail();
		} catch (RemoteAccessException e) {
			assertThat(e.getMessage(), startsWith("Cannot access HTTP invoker remote service at"));
			assertThat(e.getMessage(), containsString("status code = " + HttpStatus.SC_UNAUTHORIZED));
		}
	}

}
