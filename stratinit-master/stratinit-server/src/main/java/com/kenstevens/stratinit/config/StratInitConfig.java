package com.kenstevens.stratinit.config;

import com.kenstevens.stratinit.server.remote.mail.SMTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class StratInitConfig {
    @Autowired
    SMTPService smtpService;

    @PostConstruct
    public void init() {
        smtpService.disable();
    }
}
