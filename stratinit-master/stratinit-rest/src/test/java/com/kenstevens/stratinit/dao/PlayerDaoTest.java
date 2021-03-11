package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.BaseStratInitControllerTest;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.helper.PlayerHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PlayerDaoTest extends BaseStratInitControllerTest {
    private final String PLAYER2_NAME = "player2";
    @Autowired
    private PlayerDao playerDao;

    @Test
    public void testPlayerPersistence() {
        try {
            Player player2 = new Player(PLAYER2_NAME);
            player2.setEmail("foo@foo.com");
            playerDao.save(player2);
            assertFalse(playerMe.equals(player2));
            assertPlayer(PlayerHelper.PLAYER_ME);
            assertPlayer(PLAYER2_NAME);
        } finally {
            playerDao.deleteByUsername(PlayerHelper.PLAYER_ME);
            playerDao.deleteByUsername(PLAYER2_NAME);
        }
    }

    private void assertPlayer(String username) {
        assertNotNull(playerDao.find(username));
    }
}
