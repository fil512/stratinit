package com.kenstevens.stratinit.dao.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.model.Player;

public class PlayerDaoTest extends StratInitTest {
	@Autowired
	private PlayerDao playerDao;

	@Test
	public void testPlayerRemove() {
		String username = "TEST";
		Player player = new Player(username);
		playerDao.persist(player);
		assertNotNull(playerDao.find(username));
		playerDao.remove(player);
		assertNull(playerDao.find(username));
	}
}
