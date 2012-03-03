package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.GetUnjoinedGamesCommand;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class GetUnjoinedGamesAction extends Action {
	@Autowired
	private Spring spring;
	private GetUnjoinedGamesCommand getUnjoinedGamesCommand;

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		getUnjoinedGamesCommand = spring.getBean(GetUnjoinedGamesCommand.class);
	}
	
	@Override
	public Command<? extends Object> getCommand() {
		return getUnjoinedGamesCommand;
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}