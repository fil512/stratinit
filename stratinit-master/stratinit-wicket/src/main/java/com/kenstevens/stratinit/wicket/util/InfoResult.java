package com.kenstevens.stratinit.wicket.util;

import com.kenstevens.stratinit.remote.Result;
import org.apache.wicket.Component;

public class InfoResult<T> {

	private final Component component;

	public InfoResult(Component component) {
		this.component = component;
	}

	public void info(Result<T> result) {
		for (String message : result.getMessages()) {
			component.info(message);
		}
	}

}
