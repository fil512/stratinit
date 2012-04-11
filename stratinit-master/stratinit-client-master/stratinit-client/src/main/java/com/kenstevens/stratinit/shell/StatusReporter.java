package com.kenstevens.stratinit.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.event.StratinitEventBus;
import com.kenstevens.stratinit.remote.Result;

@Service
public class StatusReporter {
	@Autowired
	private StratinitEventBus eventBus;
	
	public void reportResult(String text) {
		Message message = new Message(text, Message.Type.RESPONSE);
		eventBus.post(new StatusReportEvent(message));
	}
	public void reportAction(String text) {
		Message message = new Message(text, Message.Type.ACTION);
		eventBus.post(new StatusReportEvent(message));
	}
	public void reportError(String text) {
		Message message = new Message(text, Message.Type.ERROR);
		eventBus.post(new StatusReportEvent(message));
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
