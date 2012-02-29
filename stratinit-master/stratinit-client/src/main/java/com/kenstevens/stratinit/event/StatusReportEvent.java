package com.kenstevens.stratinit.event;

import com.kenstevens.stratinit.ui.shell.Message;

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
