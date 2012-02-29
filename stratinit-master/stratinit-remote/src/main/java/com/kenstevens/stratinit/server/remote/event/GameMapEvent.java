package com.kenstevens.stratinit.server.remote.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.Game;

@Scope("prototype")
@Component
public class GameMapEvent extends Event {
	@Autowired
	private StratInitUpdater stratInitUpdater;

	GameMapEvent(Game game) {
		super(game, game.getExpectedMapTime());
	}

	@Override
	protected void execute() {
		Integer gameId = (Integer)getEventKey().getKey();
		stratInitUpdater.mapGame(gameId);
	}
}
