package com.kenstevens.stratinit;

import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.rest.RestClient;
import com.kenstevens.stratinit.rest.RestStratInit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class SpringConfig {
//    @Bean
//    public HttpInvokerProxyFactoryBean invoker(BasicAuthenticationCommonsHttpInvokerRequestExecutor executor) {
//        HttpInvokerProxyFactoryBean invoker = new HttpInvokerProxyFactoryBean();
//        invoker.setServiceUrl("http://localhost:8080/remote");
//        invoker.setServiceInterface(StratInit.class);
//        invoker.setHttpInvokerRequestExecutor(executor);
//        return invoker;
//    }

    @Bean
    public StratInit stratInit() {
        return new RestStratInit();
    }

    @Bean
    public RestClient restClient() {
        return new RestClient("http://localhost:8080/stratinit");
    }
}
