package com.kenstevens.stratinit.server.remote;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    // FIXME make all loggers this
    final Logger logger = LoggerFactory.getLogger(getClass());
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    public void encode() {
        logger.info("pwd: {}", encoder.encode("testy"));
    }
}