package com.kenstevens.stratinit.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.springframework.context.ApplicationContext;

import com.kenstevens.stratinit.velocity.SpringContext;

public class SpringComponentInjector implements IComponentInstantiationListener {

	@Override
	public void onInstantiation(Component component) {
		ApplicationContext context = SpringContext.getContext();
		context.getAutowireCapableBeanFactory().configureBean(component,
				getBeanName(component.getClass()));
	}
	
	private <T> String getBeanName(Class<? extends T> type) {
		String beanName = type.getSimpleName();
		return beanName.substring(0, 1).toLowerCase()
				+ beanName.substring(1);
	}
}
