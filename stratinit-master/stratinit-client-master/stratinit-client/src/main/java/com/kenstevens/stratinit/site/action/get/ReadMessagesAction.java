package com.kenstevens.stratinit.site.action.get;

import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.site.GetAction;
import com.kenstevens.stratinit.site.command.ReadMessagesCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class ReadMessagesAction extends GetAction<ReadMessagesCommand> {
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private Data db;

	protected ReadMessagesAction() {
		super(new ReadMessagesCommand());
	}

	@Override
	public void postRequest() {
		statusReporter.reportResult(db.getInbox().size() + " messages loaded.");
	}
}