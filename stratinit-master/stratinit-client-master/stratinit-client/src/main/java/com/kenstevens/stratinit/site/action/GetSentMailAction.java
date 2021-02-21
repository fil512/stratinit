package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.GetSentMailCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetSentMailAction extends Action<GetSentMailCommand> {
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private Data db;

	protected GetSentMailCommand buildCommand() {
		return new GetSentMailCommand();
	}

	@Override
	public void postRequest() {
		statusReporter.reportResult(db.getInbox().size() + " messages loaded.");
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}