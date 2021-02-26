package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.BaseStratInitClientTest;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.event.ArrivedDataEventAccumulator;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.StratInitServer;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.CommandProcessor;
import com.kenstevens.stratinit.util.Spring;
import org.jmock.Expectations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandProcessorTest extends BaseStratInitClientTest {
	@Autowired
	private CommandProcessor commandProcessor;
	@Autowired
	private Data db;
	@Autowired
	private Spring spring;

	@Test
	public void getGames() throws InterruptedException {
		final GetGamesAction getGamesAction = spring
				.getBean(GetGamesAction.class);
		Command<? extends Object> getGamesCommand = getGamesAction.getCommand();
		final StratInitServer stratInitServer = context.mock(StratInitServer.class);
		final ArrivedDataEventAccumulator arrivedDataEventAccumulator = context.mock(ArrivedDataEventAccumulator.class);
		ReflectionTestUtils.setField(getGamesCommand, "stratInitServer", stratInitServer);
		ReflectionTestUtils.setField(commandProcessor, "arrivedDataEventAccumulator", arrivedDataEventAccumulator);
		context.checking(new Expectations() {
			{
				oneOf(stratInitServer).getJoinedGames();
				will(returnValue(new Result<List<SIGame>>(makeSIGames())));
				oneOf(arrivedDataEventAccumulator).clear();
				oneOf(arrivedDataEventAccumulator).fireEvents();
			}
		});

		assertEquals(0, db.getGameList().size());
		commandProcessor.process(getGamesAction);
		context.assertIsSatisfied();
		assertEquals(1, db.getGameList().size());
		db.getGameList().remove("1");
	}
}
