package com.kenstevens.stratinit.main;

import com.kenstevens.stratinit.remote.StratInit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public class FlushCache {
	private final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private StratInit stratInit;

	@PreDestroy
	public void shutdown() {
		logger.info("Shutdown event received");
		stratInit.shutdown();
		logger.info("Shutdown event processed");
	}
}
