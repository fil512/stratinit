package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.GetRelationsCommand;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class GetRelationsAction extends Action {
	@Autowired
	private Spring spring;
	private GetRelationsCommand getRelationsCommand;

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		getRelationsCommand = spring.getBean(GetRelationsCommand.class);
	}
	
	@Override
	public Command<? extends Object> getCommand() {
		return getRelationsCommand;
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}