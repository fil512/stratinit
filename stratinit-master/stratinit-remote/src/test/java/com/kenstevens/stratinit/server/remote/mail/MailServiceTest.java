package com.kenstevens.stratinit.server.remote.mail;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Disabled
@ExtendWith(SpringExtension.class)
public class MailServiceTest {
	@Autowired
	private SMTPService smtpService;

	@Test
	public void sendMail() {
		smtpService.sendEmail("khstevens@gmail.com", "ken.stevens@sympatico.ca", "testsubj", "testbod");
	}
}
