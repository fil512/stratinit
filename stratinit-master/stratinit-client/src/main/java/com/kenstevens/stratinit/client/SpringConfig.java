package com.kenstevens.stratinit.client;

import com.kenstevens.stratinit.client.rest.StratInitRestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class SpringConfig {
    @Bean
    public StratInitRestClient stratInitRestClient() {
        return new StratInitRestClient("http://localhost:8081/stratinit");
    }

// FIXME move to swt
//    @Bean
//    public IServerConfig serverConfig() {
//        return new RestServerConfig();
//    }
}
