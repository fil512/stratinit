package com.kenstevens.stratinit.shell;

import com.kenstevens.stratinit.client.event.StratInitEvent;

// FIXME move to api
public class StatusReportEvent implements StratInitEvent {
	private final Message message;

	public StatusReportEvent(Message message) {
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}
}
