package com.kenstevens.stratinit;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gwt.event.shared.HandlerManager;
import com.kenstevens.stratinit.ui.shell.Message;
import com.kenstevens.stratinit.ui.shell.StatusReportEvent;
import com.kenstevens.stratinit.ui.shell.StatusReportEventHandler;

@Service
public final class LoggerStatusReporterListener {
	private final Log logger = LogFactory.getLog(getClass());
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
