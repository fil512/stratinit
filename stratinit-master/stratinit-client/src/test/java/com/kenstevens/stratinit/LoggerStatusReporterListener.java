package com.kenstevens.stratinit;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gwt.event.shared.HandlerManager;
import com.kenstevens.stratinit.event.StatusReportEvent;
import com.kenstevens.stratinit.event.StatusReportEventHandler;
import com.kenstevens.stratinit.ui.shell.Message;

@Service
public final class LoggerStatusReporterListener {
	private final Logger logger = Logger.getLogger(getClass());
	@Autowired
	private HandlerManager handlerManager;

	private LoggerStatusReporterListener() {}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		handlerManager.addHandler(StatusReportEvent.TYPE,
				new StatusReportEventHandler() {

					@Override
					public void reportStatus(Message message) {
						logger.info(message.toString());
					}
				});
	}
}
