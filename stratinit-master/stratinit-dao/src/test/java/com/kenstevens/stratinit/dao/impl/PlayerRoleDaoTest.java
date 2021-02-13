package com.kenstevens.stratinit.dao.impl;

import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.model.PlayerRole;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PlayerRoleDaoTest extends StratInitTest {
	@Autowired
	private PlayerDao playerDao;
	private String roleName;

	@Test
	public void testPlayerPersistenceAndRemove() {
		PlayerRole playerRole = new PlayerRole();
		playerRole.setPlayer(testPlayer1);
		roleName = "ROLE_ADMIN";
		playerRole.setRoleName(roleName);

		playerDao.save(playerRole);

		Assert.assertNotNull(playerDao.getPlayerRole(testPlayer1, roleName));
		playerDao.deleteByUsername(TEST_PLAYER1_USERNAME);
		Assert.assertNull(playerDao.getPlayerRole(testPlayer1, roleName));
	}
}
