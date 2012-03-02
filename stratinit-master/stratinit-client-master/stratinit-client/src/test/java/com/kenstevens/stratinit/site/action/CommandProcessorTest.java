package com.kenstevens.stratinit.site.action;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jmock.Expectations;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import com.kenstevens.stratinit.StratInitClientTest;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.event.ArrivedDataEventAccumulator;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.CommandProcessor;
import com.kenstevens.stratinit.util.Spring;

public class CommandProcessorTest extends StratInitClientTest {
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
		final StratInit stratInit = context.mock(StratInit.class);
		final ArrivedDataEventAccumulator arrivedDataEventAccumulator = context.mock(ArrivedDataEventAccumulator.class);
		ReflectionTestUtils.setField(getGamesCommand, "stratInit", stratInit);
		ReflectionTestUtils.setField(commandProcessor, "arrivedDataEventAccumulator", arrivedDataEventAccumulator);
		context.checking(new Expectations() {
			{
				oneOf(stratInit).getJoinedGames();
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
