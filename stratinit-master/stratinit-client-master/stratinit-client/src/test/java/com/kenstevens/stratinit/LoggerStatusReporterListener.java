package com.kenstevens.stratinit;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.event.StratinitEventBus;
import com.kenstevens.stratinit.shell.StatusReportEvent;

@Service
public final class LoggerStatusReporterListener {
	private final Log logger = LogFactory.getLog(getClass());
	@Autowired
	private StratinitEventBus eventBus;
	
	private LoggerStatusReporterListener() {}

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
