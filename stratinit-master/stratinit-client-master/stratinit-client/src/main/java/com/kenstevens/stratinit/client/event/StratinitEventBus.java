package com.kenstevens.stratinit.client.event;

import com.google.common.eventbus.EventBus;
import org.springframework.stereotype.Service;

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
