package com.kenstevens.stratinit;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteAccessException;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.model.Account;
import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.type.Constants;

@Ignore
public class SpringRemotingIntegration extends StratInitClientTest {
	private final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private StratInit stratInit;
	@Autowired
	private Account account;

	@Test
	public void testGoodLogin() throws HttpException, IOException {
		goodLogin();

		String reply = stratInit.getVersion().getValue();
		assertEquals(Constants.SERVER_VERSION, reply);
	}


	private void goodLogin() throws HttpException, IOException {
		account.setUsername("hydro");
		account.setPassword("hydro");
	}

	@Test
	public void testGetMyGames() throws HttpException, IOException {
		goodLogin();
		List<SIGame> games = stratInit.getJoinedGames().getValue();

		for (SIGame game : games) {
			logger.info("Game #" + game.id + ": " + game.name);
		}
	}

	@Test
	public void testGetSectors() throws HttpException, IOException {
		goodLogin();
		List<SISector> sectors = stratInit.getSectors().getValue();
		logger.info("GOT " + sectors.size() + " sectors!");
	}

	@Test
	public void testGetCities() throws HttpException, IOException {
		goodLogin();
		List<SICity> cities = stratInit.getCities().getValue();
		logger.info("cities: " + cities.size());
	}

	@Test(expected = RemoteAccessException.class)
	public void testBadLogin() {
		account.setUsername("hydro");
		account.setPassword("hydrox");
		String reply = stratInit.getVersion().getValue();
		assertEquals(Constants.SERVER_VERSION, reply);
	}

}
