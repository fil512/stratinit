package com.kenstevens.stratinit.ui.window;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.event.NationListArrivedEvent;
import com.kenstevens.stratinit.event.StratinitEventBus;

@Scope("prototype")
@Component
public class GameInfoWindowControl {
	@Autowired
	protected StratinitEventBus eventBus;
	
	private final GameInfoWindow window;

	public GameInfoWindowControl(GameInfoWindow window) {
		this.window = window;
	}

	@Subscribe
	public void handleNationListArrivedEvent(NationListArrivedEvent event) {
		if (window.isDisposed()) {
			return;
		}
		window.setContents();
	}
	public void addObservers() {
		eventBus.register(this);
	}

}
