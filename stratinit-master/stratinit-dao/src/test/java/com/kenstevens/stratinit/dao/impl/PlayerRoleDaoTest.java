package com.kenstevens.stratinit.dao.impl;

import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.model.PlayerRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

		assertNotNull(playerDao.getPlayerRole(testPlayer1, roleName));
		playerDao.deleteByUsername(TEST_PLAYER1_USERNAME);
		assertNull(playerDao.getPlayerRole(testPlayer1, roleName));
	}
}
