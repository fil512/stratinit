package com.kenstevens.stratinit.ui.window;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gwt.event.shared.HandlerManager;
import com.kenstevens.stratinit.event.NationListArrivedEvent;
import com.kenstevens.stratinit.event.NationListArrivedEventHandler;

@Scope("prototype")
@Component
public class GameInfoWindowControl {
	@Autowired
	private HandlerManager handlerManager;

	private final GameInfoWindow window;

	public GameInfoWindowControl(GameInfoWindow window) {
		this.window = window;
	}

	public void addObservers() {
		handlerManager.addHandler(NationListArrivedEvent.TYPE,
				new NationListArrivedEventHandler() {
					@Override
					public void dataArrived() {
						if (window.isDisposed()) {
							return;
						}
						window.setContents();
					}
				});
	}

}
