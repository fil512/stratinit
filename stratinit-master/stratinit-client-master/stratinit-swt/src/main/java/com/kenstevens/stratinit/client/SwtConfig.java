package com.kenstevens.stratinit.client;

import com.kenstevens.stratinit.client.rest.RestClient;
import com.kenstevens.stratinit.config.IServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class SwtConfig {
    @Bean
    public RestClient stratInitRestClient() {
        return new RestClient("http://localhost:8081/stratinit");
    }

    @Bean
    public IServerConfig serverConfig() {
        return new RestServerConfig(8081);
    }
}
