package com.kenstevens.stratinit.velocity;

import org.springframework.context.ApplicationContext;

public final class SpringContext {
	private static ApplicationContext context;
	
	private SpringContext() {}

	public static void setContext(ApplicationContext context) {
		SpringContext.context = context;
	}

	public static ApplicationContext getContext() {
		return context;
	}

	public static Object getBean(String name) {
		return getContext().getBean(name);
	}  
}
