package com.kenstevens.stratinit.client.site.action.get;

import com.kenstevens.stratinit.client.api.IStatusReporter;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.site.GetAction;
import com.kenstevens.stratinit.client.site.command.get.ReadMessageBoardCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class ReadMessageBoardAction extends GetAction<ReadMessageBoardCommand> {
	@Autowired
	private IStatusReporter statusReporter;
	@Autowired
	private Data db;

	protected ReadMessageBoardAction() {
		super(new ReadMessageBoardCommand());
	}

	@Override
	public void postRequest() {
		statusReporter.reportResult(db.getMessageBoard().size() + " messages loaded.");
	}
}