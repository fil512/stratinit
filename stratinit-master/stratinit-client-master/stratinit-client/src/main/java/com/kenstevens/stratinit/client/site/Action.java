package com.kenstevens.stratinit.client.site;

// TODO REF can we get rid of actions and just use commands?

import com.kenstevens.stratinit.client.event.ArrivedDataEventAccumulator;
import com.kenstevens.stratinit.client.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Scope("prototype")
@Component
public abstract class Action<T extends Command<?>> {
	@Autowired
	protected ArrivedDataEventAccumulator arrivedDataEventAccumulator;
	@Autowired
	private Spring spring;

	public abstract T getCommand();

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
