package com.kenstevens.stratinit.util;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class Spring {
	@Autowired
	private ApplicationContext context;

	public <T> T getBean(Class<? extends T> type) {
		return (T) context.getBean(getBeanName(type), type);
	}

	private <T> String getBeanName(Class<? extends T> type) {
		String beanName = type.getSimpleName();
		return beanName.substring(0, 1).toLowerCase()
				+ beanName.substring(1);
	}

	public <T> Map<String, ? extends T> getBeansOfType(Class<? extends T> type) {
		return context.getBeansOfType(type);
	}

	public <T> T autowire(T existingBean) {
		context.getAutowireCapableBeanFactory().configureBean(existingBean,
				getBeanName(existingBean.getClass()));
		return existingBean;
	}
}
