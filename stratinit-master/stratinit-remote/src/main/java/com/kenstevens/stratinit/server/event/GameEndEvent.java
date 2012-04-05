package com.kenstevens.stratinit.server.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.Game;

@Scope("prototype")
@Component
public class GameEndEvent extends Event {
	@Autowired
	private StratInitUpdater stratInitUpdater;

	GameEndEvent(Game game) {
		super(game, game.getEnds());
	}

	@Override
	protected void execute() {
		Integer gameId = (Integer)getEventKey().getKey();
		stratInitUpdater.endGame(gameId);

	}
}
