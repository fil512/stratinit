package com.kenstevens.stratinit.client.server.event;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.server.event.svc.StratInitUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GameStartEvent extends Event {
	@Autowired
	private StratInitUpdater stratInitUpdater;

	GameStartEvent(Game game) {
		super(game, game.getStartTime());
	}

	@Override
	protected void execute() {
		Integer gameId = (Integer) getEventKey().getKey();
		stratInitUpdater.startGame(gameId);

	}
}
