package com.kenstevens.stratinit.client.server.rest.mail;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Disabled
@ExtendWith(SpringExtension.class)
public class ManualMailServiceTest {
    @Autowired
    private SMTPService smtpService;

    @Test
    public void sendMail() {
        smtpService.sendEmail("khstevens@gmail.com", "ken.stevens@sympatico.ca", "testsubj", "testbod");
    }
}
