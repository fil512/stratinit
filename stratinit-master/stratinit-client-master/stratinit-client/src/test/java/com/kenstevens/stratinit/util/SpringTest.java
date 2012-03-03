package com.kenstevens.stratinit.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import com.kenstevens.stratinit.StratInitClientTest;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.site.command.SendMessageCommand;

public class SpringTest extends StratInitClientTest {
	@Autowired protected Spring spring;

	@Test
	public void testSpringAutowire() {
		Mail mail = new Mail();
		SendMessageCommand sendMessageCommand = new SendMessageCommand(
				mail);
		assertNull(ReflectionTestUtils.getField(sendMessageCommand,
				"statusReporter"));
		spring.autowire(sendMessageCommand);
		assertNotNull(ReflectionTestUtils.getField(sendMessageCommand,
				"statusReporter"));
	}
}
