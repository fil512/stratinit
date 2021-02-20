package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.GetMyNationCommand;
import com.kenstevens.stratinit.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Scope("prototype")
@Component
public class GetMyNationAction extends Action {
	@Autowired
	private Spring spring;
	private GetMyNationCommand getMyNationCommand;

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		getMyNationCommand = spring.getBean(GetMyNationCommand.class);
	}
	
	@Override
	public Command<? extends Object> getCommand() {
		return getMyNationCommand;
	}

	@Override
	public boolean canRepeat() {
		return false;
	}
}