package com.kenstevens.stratinit.server.remote.event;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.QuiesceService;

@Service
public class EventSchedulerQuiescer implements QuiesceService {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private EventScheduler eventScheduler;

	@Override
	public void quiesce() {
		logger.info("Shutting down event scheduler.");
		eventScheduler.shutdown();
	}

}
