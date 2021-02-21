package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.BaseStratInitWebTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PlayerDaoServiceTest extends BaseStratInitWebTest {
    @Autowired
    private PlayerDaoService playerDaoServiceImpl;
    private final String UNAME = "uname";

    private final Player testPlayer = new Player(UNAME);

    @BeforeEach
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
