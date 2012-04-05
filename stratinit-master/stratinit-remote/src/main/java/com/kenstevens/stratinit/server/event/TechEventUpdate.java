package com.kenstevens.stratinit.server.event;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;

@Scope("prototype")
@Component
public class TechEventUpdate extends EventUpdate {
	@Autowired
	private GameDaoService gameDaoService;

	private final Date date;

	public TechEventUpdate(Date date) {
		this.date = date;
	}

	@Override
	protected void executeWrite() {
		Game game = getGame();
		gameDaoService.updateGame(game, date);
	}
}
