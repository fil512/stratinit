package com.kenstevens.stratinit.event;

import org.springframework.stereotype.Service;

import com.google.gwt.event.shared.HandlerManager;

@Service
public class HandlerManagerService extends HandlerManager {
	public HandlerManagerService() {
		super(null, false);
	}
}
