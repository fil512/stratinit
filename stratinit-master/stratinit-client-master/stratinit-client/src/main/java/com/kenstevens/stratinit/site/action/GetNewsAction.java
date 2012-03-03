package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.GetNewsCommand;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class GetNewsAction extends Action {
	@Autowired
	private Spring spring;
	private GetNewsCommand getNewsCommand;

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		getNewsCommand = spring.getBean(GetNewsCommand.class);
	}
	
	@Override
	public Command<? extends Object> getCommand() {
		return getNewsCommand;
	}

	@Override
	public boolean canRepeat() {
		return false;
	}
}