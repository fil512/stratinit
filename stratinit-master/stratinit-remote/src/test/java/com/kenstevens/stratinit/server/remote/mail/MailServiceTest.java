package com.kenstevens.stratinit.server.remote.mail;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/WEB-INF/applicationContext.xml")
public class MailServiceTest {
	@Autowired
	private SMTPService smtpService;

	@Test
	public void sendMail() {
		smtpService.sendEmail("khstevens@gmail.com", "ken.stevens@sympatico.ca", "testsubj", "testbod");
	}
}
