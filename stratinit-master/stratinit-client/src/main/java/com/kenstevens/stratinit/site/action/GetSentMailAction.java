package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.GetSentMailCommand;
import com.kenstevens.stratinit.ui.shell.StatusReporter;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class GetSentMailAction extends Action {
	@Autowired
	private Spring spring;
	private GetSentMailCommand getSentMailCommand;
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private Data db;

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		getSentMailCommand = spring.getBean(GetSentMailCommand.class);
	}
	
	@Override
	public Command<? extends Object> getCommand() {
		return getSentMailCommand;
	}

	@Override
	public void postRequest() {
		statusReporter.reportResult(db.getInbox().size()+" messages loaded.");
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}