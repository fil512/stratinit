package com.kenstevens.stratinit.event;

import org.springframework.stereotype.Service;

import com.google.common.eventbus.EventBus;

@Service
public class StratinitEventBus {
	private final EventBus eventBus = new EventBus();
	
	public void register(Object handler) {
		eventBus.register(handler);
	}

	public void post(StratInitEvent event) {
		eventBus.post(event);
	}
	
	
}
