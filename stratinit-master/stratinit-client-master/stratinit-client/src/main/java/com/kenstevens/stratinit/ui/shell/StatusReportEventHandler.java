package com.kenstevens.stratinit.ui.shell;

import com.google.gwt.event.shared.EventHandler;

public abstract class StatusReportEventHandler implements EventHandler {
	abstract public void reportStatus(Message message);
}
