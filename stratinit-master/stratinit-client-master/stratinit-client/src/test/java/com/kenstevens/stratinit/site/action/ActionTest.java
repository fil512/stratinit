package com.kenstevens.stratinit.site.action;

import org.jmock.Expectations;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import com.kenstevens.stratinit.StratInitClientTest;
import com.kenstevens.stratinit.site.ActionQueue;
import com.kenstevens.stratinit.util.Spring;



public class ActionTest extends StratInitClientTest {
	@Autowired
	private Spring spring;

	@Test
	public void addToActionQueue() throws InterruptedException {
		final GetGamesAction getGamesAction = spring.getBean(GetGamesAction.class);
		final ActionQueue actionQueue = context.mock(ActionQueue.class);
		ReflectionTestUtils.setField(getGamesAction, "actionQueue", actionQueue);

		context.checking(new Expectations() {
			{
				oneOf(actionQueue).put(getGamesAction);
			}
		});
		getGamesAction.addToActionQueue();
		context.assertIsSatisfied();
	}
}
