package com.kenstevens.stratinit.client.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
// FIXME remove this class (maybe)
public class Spring {
	@Autowired
	private ApplicationContext context;

	public <T> T getBean(Class<? extends T> type) {
		return context.getBean(getBeanName(type), type);
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
