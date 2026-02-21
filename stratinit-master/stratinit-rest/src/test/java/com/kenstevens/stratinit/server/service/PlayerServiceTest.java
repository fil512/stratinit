package com.kenstevens.stratinit.server.service;

import com.kenstevens.stratinit.BaseStratInitControllerTest;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.remote.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PlayerServiceTest extends BaseStratInitControllerTest {
    private final String UNAME = "uname";
    private final Player testPlayer = new Player(UNAME);
    @Autowired
    private PlayerService playerServiceImpl;

    @BeforeEach
    public void setPlayerFields() {
        testPlayer.setEmail("foo@bar.com");
    }

    @Test
    public void testRegister() {
        Result<Player> result = playerServiceImpl.register(testPlayer);
        assertResult(result);
        assertNotNull(playerDao.find(UNAME));
    }

    @Test
    public void testRegisterUserExists() {
        Result<Player> result = playerServiceImpl.register(testPlayer);
        result = playerServiceImpl.register(testPlayer);
        assertFalseResult(result);
    }

    @Test
    public void testRegisterBadEmail() {
        testPlayer.setEmail("foo");
        Result<Player> result = playerServiceImpl.register(testPlayer);
        assertFalseResult(result);
    }
}
