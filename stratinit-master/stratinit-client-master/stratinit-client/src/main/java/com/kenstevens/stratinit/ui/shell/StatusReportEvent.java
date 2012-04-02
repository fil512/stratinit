package com.kenstevens.stratinit.ui.shell;

import com.kenstevens.stratinit.event.StratInitEvent;

public class StatusReportEvent extends StratInitEvent<StatusReportEventHandler> {
	public static final Type<StatusReportEventHandler> TYPE = new Type<StatusReportEventHandler>();
	private final Message message;
	
	public StatusReportEvent(Message message) {
		this.message = message;
	}

	@Override
	protected void dispatch(StatusReportEventHandler handler) {
		handler.reportStatus(message);
	}

}
