package com.kenstevens.stratinit.server.event;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.server.event.svc.StratInitUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
		Integer gameId = (Integer) getEventKey().getKey();
		stratInitUpdater.endGame(gameId);

	}
}
