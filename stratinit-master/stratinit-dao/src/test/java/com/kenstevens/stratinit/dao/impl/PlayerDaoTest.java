package com.kenstevens.stratinit.dao.impl;

import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.model.Player;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class PlayerDaoTest extends StratInitTest {
	@Autowired
	private PlayerDao playerDao;

	@Test
	public void testPlayerRemove() {
        String username = "TEST";
        Player player = new Player(username);
        playerDao.save(player);
        assertNotNull(playerDao.find(username));
        playerDao.delete(player);
        assertNull(playerDao.find(username));
    }
}
