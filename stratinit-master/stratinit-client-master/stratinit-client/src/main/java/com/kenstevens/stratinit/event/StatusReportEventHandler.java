package com.kenstevens.stratinit.event;

import com.google.gwt.event.shared.EventHandler;
import com.kenstevens.stratinit.ui.shell.Message;

public abstract class StatusReportEventHandler implements EventHandler {
	abstract public void reportStatus(Message message);
}
