package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.QGame;
import com.kenstevens.stratinit.client.model.QPlayer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GameRepoTest extends StratInitTest {
    @Autowired
    GameRepo gameRepo;

    @Test
    public void gameRepoTest() {
        Game game = new Game("zunk");
        List<Game> games = gameRepo.findAll();
        assertThat(games, hasSize(0));
        gameRepo.save(game);
        games = gameRepo.findAll();
        assertThat(games, hasSize(1));
        assertEquals(games.get(0).getGamename(), "zunk");

        // Test with QueryDSL
        QPlayer player = QPlayer.player;
        List<Game> foundGames = new ArrayList<>();
        gameRepo.findAll(QGame.game.gamename.eq("zunk")).forEach(foundGames::add);
        assertThat(foundGames, hasSize(1));
        assertEquals(foundGames.get(0).getGamename(), "zunk");
    }

}