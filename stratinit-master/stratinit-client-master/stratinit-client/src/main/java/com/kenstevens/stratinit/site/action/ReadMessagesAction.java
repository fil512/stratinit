package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.ReadMessagesCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class ReadMessagesAction extends Action<ReadMessagesCommand> {
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private Data db;

	protected ReadMessagesCommand buildCommand() {
		return new ReadMessagesCommand();
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