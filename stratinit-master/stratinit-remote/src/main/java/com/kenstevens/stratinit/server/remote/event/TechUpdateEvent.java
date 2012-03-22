package com.kenstevens.stratinit.server.remote.event;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.Game;

@Scope("prototype")
@Component
public class TechUpdateEvent extends Event {
	@Autowired
	private StratInitUpdater stratInitUpdater;

	TechUpdateEvent(Game game) {
		super(game);
	}

	@Override
	protected void execute() {
		Integer gameId = (Integer)getEventKey().getKey();
		stratInitUpdater.updateTech(gameId,  new Date());
	}
}
