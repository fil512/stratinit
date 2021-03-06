package com.kenstevens.stratinit.client.site;

// TODO REF can we get rid of actions and just use commands?

import com.kenstevens.stratinit.client.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;


public abstract class GetAction<T extends Command<?>> extends Action<T> {
	private final T command;
	@Autowired
	private Spring spring;

	protected GetAction(T command) {
		this.command = command;
	}

	@PostConstruct
	public void inititialize() {
		spring.autowire(command);
	}

	@Override
	public T getCommand() {
		return command;
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

	// FIXME remove once this is gone in superclass
	@Override
	protected T buildCommand() {
		throw new UnsupportedOperationException();
	}
}
