package com.kenstevens.stratinit.server.daoservice;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.StratInitWebBase;

public class PlayerDaoServiceTest extends StratInitWebBase {
	@Autowired
	private PlayerDaoService playerDaoServiceImpl;
	private final String UNAME = "uname";
	
	private final Player testPlayer = new Player(UNAME);

	@Before
	public void setPlayerFields() {
		testPlayer.setEmail("foo@bar.com");
	}
	
	@Test
	public void testRegister() {
		Result<Player> result = playerDaoServiceImpl.register(testPlayer);
		assertResult(result);
		assertNotNull(playerDao.find(UNAME));
	}

	@Test
	public void testRegisterUserExists() {
		Result<Player> result = playerDaoServiceImpl.register(testPlayer);
		result = playerDaoServiceImpl.register(testPlayer);
		assertFalseResult(result);
	}

	@Test
	public void testRegisterBadEmail() {
		testPlayer.setEmail("foo");
		Result<Player> result = playerDaoServiceImpl.register(testPlayer);
		assertFalseResult(result);
	}
}
