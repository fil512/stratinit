package com.kenstevens.stratinit.server.remote.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Scope("prototype")
@Component
public class EndGameEventUpdate extends EventUpdate {
	@Autowired
	private GameEnder gameEnder;

	@Override
	protected void executeWrite() {
		gameEnder.endGame(getGame());
	}
}
