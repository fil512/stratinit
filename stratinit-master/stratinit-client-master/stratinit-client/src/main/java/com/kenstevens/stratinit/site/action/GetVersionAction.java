package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.GetVersionCommand;
import com.kenstevens.stratinit.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Scope("prototype")
@Component
public class GetVersionAction extends Action {
	@Autowired
	private Spring spring;
	private GetVersionCommand getVersionCommand;

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		getVersionCommand = spring.getBean(GetVersionCommand.class);
	}
	
	@Override
	public Command<? extends Object> getCommand() {
		return getVersionCommand;
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}