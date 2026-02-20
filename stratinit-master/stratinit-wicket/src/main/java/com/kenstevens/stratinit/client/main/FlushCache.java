package com.kenstevens.stratinit.client.main;

import com.kenstevens.stratinit.svc.StratInitAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

@Service
public class FlushCache {
	private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StratInitAdmin stratInitAdmin;

	@PreDestroy
	public void shutdown() {
        logger.info("Shutdown event received");
        stratInitAdmin.shutdown();
        logger.info("Shutdown event processed");
    }
}
