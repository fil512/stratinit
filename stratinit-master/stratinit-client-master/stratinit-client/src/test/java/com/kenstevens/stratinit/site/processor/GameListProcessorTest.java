package com.kenstevens.stratinit.site.processor;

import com.kenstevens.stratinit.BaseStratInitClientTest;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.model.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameListProcessorTest extends BaseStratInitClientTest {
    @Autowired
    private GameListProcessor gameListProcessor;
    @Autowired
    private Data db;

    @Test
    public void canProcess() {
        List<SIGame> sigames = makeSIGames();

        gameListProcessor.process(sigames);
		assertEquals(1, db.getGameList().size());
		assertEquals("test", db.getGameList().get(1).getGamename());
		db.getGameList().remove("1");
	}
}
