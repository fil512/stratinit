package com.kenstevens.stratinit.web.wicket;

import org.apache.wicket.Component;

import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;

public class InfoResult {

	private final Component component;

	public InfoResult(Component component) {
		this.component = component;
	}

	public void info(Result<None> result) {
		for (String message : result.getMessages()) {
			component.info(message);
		}
	}

}
