package com.kenstevens.stratinit.server.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.QuiesceService;

@Service
public class EventSchedulerQuiescer implements QuiesceService {
	private final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private EventScheduler eventScheduler;

	@Override
	public void quiesce() {
		logger.info("Shutting down event scheduler.");
		eventScheduler.shutdown();
	}

}
