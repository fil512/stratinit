package com.kenstevens.stratinit.spring;

import com.kenstevens.stratinit.remote.StratInit;

public final class StratInitContext {
	private StratInitContext() {}
	
	public static StratInit getStratInit() {
		return (StratInit)SpringContext.getBean("stratInit");
	}
}
