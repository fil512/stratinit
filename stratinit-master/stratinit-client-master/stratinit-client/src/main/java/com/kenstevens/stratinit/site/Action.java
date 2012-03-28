package com.kenstevens.stratinit.site;

// TODO REF can we get rid of actions and just use commands?
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.event.ArrivedDataEventAccumulator;


@Scope("prototype")
@Component
public abstract class Action {
	@Autowired
	private ActionQueue actionQueue;
	@Autowired
	protected ArrivedDataEventAccumulator arrivedDataEventAccumulator;
	
	public abstract Command<? extends Object> getCommand();
	
	public final void addToActionQueue() {
		try {
			actionQueue.put(this);
		} catch (InterruptedException e) {
		}
	}
	
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
