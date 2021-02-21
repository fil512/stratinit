package com.kenstevens.stratinit.site;

// TODO REF can we get rid of actions and just use commands?

import com.kenstevens.stratinit.event.ArrivedDataEventAccumulator;
import com.kenstevens.stratinit.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Scope("prototype")
@Component
public abstract class Action<T extends Command<?>> {
	protected T command;
	@Autowired
	protected ArrivedDataEventAccumulator arrivedDataEventAccumulator;
	@Autowired
	private Spring spring;

	public final T getCommand() {
		return command;
	}

	@PostConstruct
	protected final void initialize() {
		command = spring.autowire(buildCommand());
	}

	protected abstract T buildCommand();

	public void preRequest() {
	}

	public void postRequest() {
	}

	public boolean canRepeat() {
		return true;
	}
	
	public String getDescription() {
		return getCommand().getDescription();
	}

	public void postEvents() {
	}
}
