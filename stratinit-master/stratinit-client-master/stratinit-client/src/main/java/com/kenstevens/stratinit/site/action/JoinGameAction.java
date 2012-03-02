package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.JoinGameCommand;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class JoinGameAction extends Action {
	@Autowired
	private Spring spring;

	private final int gameId;
	private final boolean noAlliances;
	private JoinGameCommand joinGameCommand;

	public JoinGameAction(Integer gameId, boolean noAlliances) {
		this.gameId = gameId;
		this.noAlliances = noAlliances;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		joinGameCommand = spring.autowire(new JoinGameCommand( gameId, noAlliances ));
	}

	@Override
	public Command<? extends Object> getCommand() {
		return joinGameCommand;
	}
}