package com.kenstevens.stratinit.server.daoservice;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.StratInitWebBase;

public class PlayerDaoServiceTest extends StratInitWebBase {
	@Autowired
	private PlayerDaoService playerDaoServiceImpl;

	@Test
	public void testRegister() {
		Result<Player> result = playerDaoServiceImpl.register("uname", "pwd", "foo@foo.com");
		assertResult(result);
		assertNotNull(playerDao.find("uname"));
	}

	@Test
	public void testRegisterUserExists() {
		Result<Player> result = playerDaoServiceImpl.register("uname", "pwd", "foo@foo.com");
		result = playerDaoServiceImpl.register("uname", "pwd", "foo@foo.com");
		assertFalseResult(result);
	}

	@Test
	public void testRegisterBadEmail() {
		Result<Player> result = playerDaoServiceImpl.register("uname", "pwd", "foofoo.com");
		assertFalseResult(result);
	}

}
