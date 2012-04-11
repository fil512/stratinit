package com.kenstevens.stratinit.shell;

import com.kenstevens.stratinit.event.StratInitEvent;

public class StatusReportEvent implements StratInitEvent {
	private final Message message;
	
	public StatusReportEvent(Message message) {
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}
}
