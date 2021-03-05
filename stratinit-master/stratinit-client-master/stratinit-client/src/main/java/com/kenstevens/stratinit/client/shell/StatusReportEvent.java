package com.kenstevens.stratinit.client.shell;

import com.kenstevens.stratinit.client.event.StratInitEvent;

public class StatusReportEvent implements StratInitEvent {
	private final Message message;
	
	public StatusReportEvent(Message message) {
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}
}
