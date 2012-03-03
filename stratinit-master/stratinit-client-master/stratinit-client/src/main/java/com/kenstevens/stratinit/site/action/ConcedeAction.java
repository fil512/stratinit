package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.ConcedeCommand;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class ConcedeAction extends Action {
	@Autowired
	private Spring spring;
	private ConcedeCommand concedeCommand;

	public ConcedeAction() {
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		concedeCommand = spring.autowire(new ConcedeCommand());
	}

	@Override
	public Command<? extends Object> getCommand() {
		return concedeCommand;
	}
}