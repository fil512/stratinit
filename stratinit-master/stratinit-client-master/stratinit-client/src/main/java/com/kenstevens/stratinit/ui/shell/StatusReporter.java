package com.kenstevens.stratinit.ui.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gwt.event.shared.HandlerManager;
import com.kenstevens.stratinit.remote.Result;

@Service
public class StatusReporter {
	@Autowired
	private HandlerManager handlerManager;
	
	public void reportResult(String text) {
		Message message = new Message(text, Message.Type.RESPONSE);
		handlerManager.fireEvent(new StatusReportEvent(message));
	}
	public void reportAction(String text) {
		Message message = new Message(text, Message.Type.ACTION);
		handlerManager.fireEvent(new StatusReportEvent(message));
	}
	public void reportError(String text) {
		Message message = new Message(text, Message.Type.ERROR);
		handlerManager.fireEvent(new StatusReportEvent(message));
	}
	public void reportError(Exception e) {
		if (e != null && e.getMessage() != null) {
			reportError(e.getMessage());
		} else {
			reportError(e.getClass().getName());
		}
	}

	public void reportLoginError() {
		reportError("Not logged in.  Please login before performing any actions.");
	}

	public <T> void reportResult(Result<T> result) {
		if (result.isSuccess() && result.isMoveSuccess()) {
			reportAction(result.toString());
		} else {
			reportError(result.toString());
		}
	}
}
