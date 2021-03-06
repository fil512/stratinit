package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.BaseStratInitControllerTest;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.remote.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PlayerDaoServiceTest extends BaseStratInitControllerTest {
    private final String UNAME = "uname";
    private final Player testPlayer = new Player(UNAME);
    @Autowired
    private PlayerDaoService playerDaoServiceImpl;

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
