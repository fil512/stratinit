package com.kenstevens.stratinit.site;

// TODO REF can we get rid of actions and just use commands?

import com.kenstevens.stratinit.event.ArrivedDataEventAccumulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Scope("prototype")
@Component
public abstract class Action {
	@Autowired
	protected ArrivedDataEventAccumulator arrivedDataEventAccumulator;

	public abstract Command<? extends Object> getCommand();
	
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
