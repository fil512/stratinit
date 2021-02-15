package com.kenstevens.stratinit;

import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.site.server.BasicAuthenticationCommonsHttpInvokerRequestExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

@Configuration
@ComponentScan
public class SpringConfig {
    @Bean
    public HttpInvokerProxyFactoryBean invoker(BasicAuthenticationCommonsHttpInvokerRequestExecutor executor) {
        HttpInvokerProxyFactoryBean invoker = new HttpInvokerProxyFactoryBean();
        invoker.setServiceUrl("http://localhost:8080/remote");
        invoker.setServiceInterface(StratInit.class);
        invoker.setHttpInvokerRequestExecutor(executor);
        return invoker;
    }
}
