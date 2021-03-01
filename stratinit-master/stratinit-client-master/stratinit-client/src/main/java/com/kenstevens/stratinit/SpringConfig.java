package com.kenstevens.stratinit;

import com.kenstevens.stratinit.rest.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class SpringConfig {
    @Bean
    public RestClient restClient() {
        return new RestClient("http://localhost:8081/stratinit");
    }
}
