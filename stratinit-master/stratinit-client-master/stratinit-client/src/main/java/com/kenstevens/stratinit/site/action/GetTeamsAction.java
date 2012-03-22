package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.GetTeamsCommand;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class GetTeamsAction extends Action {
	@Autowired
	private Spring spring;
	private GetTeamsCommand getTeamsCommand;

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		getTeamsCommand = spring.getBean(GetTeamsCommand.class);
	}

	@Override
	public Command<? extends Object> getCommand() {
		return getTeamsCommand;
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}