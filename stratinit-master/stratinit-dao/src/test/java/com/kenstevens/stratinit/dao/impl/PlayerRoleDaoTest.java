package com.kenstevens.stratinit.dao.impl;

import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.PlayerRole;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.NoResultException;

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
	}

	@Test(expected=NoResultException.class)
	public void removeRole() {
		String username = "testRemove";
		Player player = new Player(username);
		playerDao.save(player);
		Assert.assertNotNull(playerDao.getPlayerRole(player, roleName));
		playerDao.remove(username);
		Assert.assertNull(playerDao.getPlayerRole(player, roleName));
	}
}
