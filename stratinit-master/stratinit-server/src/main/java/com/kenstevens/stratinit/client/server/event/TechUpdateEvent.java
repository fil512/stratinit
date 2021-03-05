package com.kenstevens.stratinit.client.server.event;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.server.event.svc.StratInitUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Scope("prototype")
@Component
public class TechUpdateEvent extends Event {
	@Autowired
	private StratInitUpdater stratInitUpdater;

	public TechUpdateEvent(Game game) {
		super(game);
	}

	@Override
	public void execute() {
		Integer gameId = (Integer) getEventKey().getKey();
		stratInitUpdater.updateTech(gameId, new Date());
	}
}
