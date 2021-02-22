package com.kenstevens.stratinit.config;

import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.server.remote.StratInitImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

@Configuration
public class SpringRemotingConfig {
    @Bean(name = "/remote")
    HttpInvokerServiceExporter stratInitRemote() {
        HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
        exporter.setService(stratinitSvc());
        exporter.setServiceInterface(StratInit.class);
        return exporter;
    }

    @Bean
    StratInit stratinitSvc() {
        return new StratInitImpl();
    }
}
