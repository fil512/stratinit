package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.SetGameCommand;
import com.kenstevens.stratinit.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Scope("prototype")
@Component
public class SetGameAction extends Action {
	private final int gameId;
	@Autowired
	private Spring spring;

	private SetGameCommand setGameCommand;
	private final boolean noAlliances;

	public SetGameAction(Integer gameId, boolean noAlliances) {
		this.gameId = gameId;
		this.noAlliances = noAlliances;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		setGameCommand = spring.autowire(new SetGameCommand( gameId , noAlliances ));
	}

	@Override
	public Command<? extends Object> getCommand() {
		return setGameCommand;
	}
}