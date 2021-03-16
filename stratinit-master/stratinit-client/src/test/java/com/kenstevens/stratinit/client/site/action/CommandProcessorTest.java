package com.kenstevens.stratinit.client.site.action;

import com.kenstevens.stratinit.BaseMockStratInitClientTest;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.site.Command;
import com.kenstevens.stratinit.client.site.CommandProcessor;
import com.kenstevens.stratinit.client.site.action.get.GetGamesAction;
import com.kenstevens.stratinit.client.util.Spring;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.remote.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommandProcessorTest extends BaseMockStratInitClientTest {
    @Autowired
    private CommandProcessor commandProcessor;
    @Autowired
    private Data db;
    @Autowired
    private Spring spring;

    @Test
    public void getGames() {
        when(stratInitServer.getJoinedGames()).thenReturn(new Result<List<SIGame>>(makeSIGames()));

        final GetGamesAction getGamesAction = spring.getBean(GetGamesAction.class);
        Command<? extends Object> getGamesCommand = getGamesAction.getCommand();

        assertEquals(0, db.getGameList().size());
        commandProcessor.process(getGamesAction);

        verify(stratInitServer).getJoinedGames();
        verify(arrivedDataEventAccumulator).clear();
        verify(arrivedDataEventAccumulator).fireEvents();

        assertEquals(1, db.getGameList().size());
        db.getGameList().remove("1");
    }
}
