package com.kenstevens.stratinit.shell;

import com.google.gwt.event.shared.EventHandler;

public abstract class StatusReportEventHandler implements EventHandler {
	abstract public void reportStatus(Message message);
}
