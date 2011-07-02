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
	private final int gameId;
	@Autowired
	private Spring spring;

	private JoinGameCommand joinGameCommand;

	public JoinGameAction(Integer gameId) {
		this.gameId = gameId;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		joinGameCommand = spring.autowire(new JoinGameCommand( gameId ));
	}

	@Override
	public Command<? extends Object> getCommand() {
		return joinGameCommand;
	}
}