package com.kenstevens.stratinit.main;

import com.kenstevens.stratinit.remote.StratInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public class FlushCache {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private StratInit stratInit;

	@PreDestroy
	public void shutdown() {
		logger.info("Shutdown event received");
		stratInit.shutdown();
		logger.info("Shutdown event processed");
	}
}
