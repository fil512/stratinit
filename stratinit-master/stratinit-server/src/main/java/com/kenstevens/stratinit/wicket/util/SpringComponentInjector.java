package com.kenstevens.stratinit.wicket.util;

import java.lang.annotation.Annotation;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.springframework.context.ApplicationContext;

import com.kenstevens.stratinit.spring.SpringContext;

public class SpringComponentInjector implements IComponentInstantiationListener {

	@Override
	public void onInstantiation(Component component) {
		if (!isSpringComponent(component)) {
			return;
		}
		ApplicationContext context = SpringContext.getContext();
		context.getAutowireCapableBeanFactory().configureBean(component,
				getBeanName(component.getClass()));
	}
	
	private boolean isSpringComponent(Component component) {
		// Only wire Spring Components
		for (Annotation annotation : component.getClass().getAnnotations()) {
			if (org.springframework.stereotype.Component.class.isAssignableFrom(annotation.annotationType())) {
				return true;
			}
		}
		return false;
	}

	private <T> String getBeanName(Class<? extends T> type) {
		String beanName = type.getSimpleName();
		return beanName.substring(0, 1).toLowerCase()
				+ beanName.substring(1);
	}
}
