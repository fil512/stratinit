package com.kenstevens.stratinit.client.util;

import com.kenstevens.stratinit.BaseStratInitClientTest;
import com.kenstevens.stratinit.client.model.Mail;
import com.kenstevens.stratinit.client.site.command.SendMessageCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SpringTest extends BaseStratInitClientTest {
    @Autowired
    protected Spring spring;

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
