package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.GetNationsCommand;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class GetNationsAction extends Action {
	@Autowired
	private Spring spring;
	private GetNationsCommand getNationsCommand;

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		getNationsCommand = spring.getBean(GetNationsCommand.class);
	}
	
	@Override
	public Command<? extends Object> getCommand() {
		return getNationsCommand;
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}