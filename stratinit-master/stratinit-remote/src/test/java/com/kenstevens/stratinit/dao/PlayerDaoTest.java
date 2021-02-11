package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.server.remote.StratInitWebBase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertFalse;

public class PlayerDaoTest extends StratInitWebBase {
	@Autowired
	private PlayerDao playerDao;
	private final String PLAYER2_NAME = "player2";

	@Test
	public void testPlayerPersistence() {
		try {
			Player player2 = new Player(PLAYER2_NAME);
			player2.setEmail("foo@foo.com");
			playerDao.save(player2);
			assertFalse(playerMe.equals(player2));
			assertPlayer(PLAYER_ME_NAME);
			assertPlayer(PLAYER2_NAME);
		} finally {
			playerDao.remove(PLAYER_ME_NAME);
			playerDao.remove(PLAYER2_NAME);
		}
	}

	private void assertPlayer(String username) {
		Assert.assertNotNull(playerDao.find(username));
	}
}
