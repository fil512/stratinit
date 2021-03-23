package com.kenstevens.stratinit.client.api;

import com.kenstevens.stratinit.client.event.StratInitEvent;

public class StatusReportEvent implements StratInitEvent {
	private final ShellMessage shellMessage;

	public StatusReportEvent(ShellMessage shellMessage) {
		this.shellMessage = shellMessage;
	}

	public ShellMessage getMessage() {
		return shellMessage;
	}
}
