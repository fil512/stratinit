package com.kenstevens.stratinit;

import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.client.event.StratinitEventBus;
import com.kenstevens.stratinit.client.shell.StatusReportEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public final class LoggerStatusReporterListener {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private StratinitEventBus eventBus;

	private LoggerStatusReporterListener() {
	}

	@Subscribe
	public void handleStatusReportEvent(StatusReportEvent event) {
		logger.info(event.getMessage().toString());
	}
	
	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		eventBus.register(this);
	}
}
