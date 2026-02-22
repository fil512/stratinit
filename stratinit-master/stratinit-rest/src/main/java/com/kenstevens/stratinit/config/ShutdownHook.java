package com.kenstevens.stratinit.config;

import com.kenstevens.stratinit.server.rest.ServerManager;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShutdownHook {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ServerManager serverManager;

    @PreDestroy
    public void shutdown() {
        logger.info("Shutdown event received");
        serverManager.shutdown();
        logger.info("Shutdown event processed");
    }
}
