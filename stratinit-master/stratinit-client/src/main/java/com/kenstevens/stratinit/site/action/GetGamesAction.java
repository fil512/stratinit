package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.GetGamesCommand;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class GetGamesAction extends Action {
	@Autowired
	private Spring spring;
	private GetGamesCommand getGamesCommand;

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		getGamesCommand = spring.getBean(GetGamesCommand.class);
	}
	
	@Override
	public Command<? extends Object> getCommand() {
		return getGamesCommand;
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}