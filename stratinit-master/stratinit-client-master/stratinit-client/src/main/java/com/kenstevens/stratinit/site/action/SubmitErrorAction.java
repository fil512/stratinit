package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.SubmitErrorCommand;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class SubmitErrorAction extends Action {
	@Autowired
	private Spring spring;
	private SubmitErrorCommand submitErrorCommand;
	private final Exception exception;

	public SubmitErrorAction(Exception e) {
		this.exception = e;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		submitErrorCommand = spring.autowire(new SubmitErrorCommand(exception));
	}

	@Override
	public Command<? extends Object> getCommand() {
		return submitErrorCommand;
	}
}