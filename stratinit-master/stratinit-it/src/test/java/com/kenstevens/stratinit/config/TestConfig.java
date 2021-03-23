package com.kenstevens.stratinit.config;

import com.kenstevens.stratinit.client.api.IStatusReporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean
    IStatusReporter statusReporter() {
        return new TestStatusReporter();
    }

}
