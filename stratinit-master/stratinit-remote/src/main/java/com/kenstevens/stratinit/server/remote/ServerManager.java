package com.kenstevens.stratinit.server.remote;

import com.kenstevens.stratinit.QuiesceService;
import com.kenstevens.stratinit.cache.Cacheable;
import com.kenstevens.stratinit.main.Spring;
import com.kenstevens.stratinit.server.remote.state.ServerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ServerManager {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private Spring spring;
	@Autowired
	private ServerStatus serverStatus;

	public void shutdown() {
		logger.info("Server shutting down...");
		serverStatus.setShutDown();
		Cacheable.setFinalFlush(true);
		Map<String, ? extends QuiesceService> beanMap = spring
				.getBeansOfType(QuiesceService.class);
		for (QuiesceService quiescer : beanMap.values()) {
			quiescer.quiesce();
		}
		logger.info("... server shutdown complete.");
	}
}
