package com.kenstevens.stratinit.client.ui.window;

import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.client.event.NationListArrivedEvent;
import com.kenstevens.stratinit.client.event.StratinitEventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
