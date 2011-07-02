package com.kenstevens.stratinit.server.velocity;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class ApplicationContextProvider implements ApplicationContextAware {

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		SpringContext.setContext(applicationContext);
		
	}

}
