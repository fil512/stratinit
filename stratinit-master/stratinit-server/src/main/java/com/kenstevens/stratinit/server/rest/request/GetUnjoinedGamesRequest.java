package com.kenstevens.stratinit.server.rest.request;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Scope("prototype")
@Component
public class GetUnjoinedGamesRequest extends PlayerRequest<List<SIGame>> {
	@Autowired
	private GameDaoService gameDaoService;

	@Override
	protected List<SIGame> execute() {
		return gameDaoService.getUnjoinedGames(getPlayer()).stream()
				.map(game -> new SIGame(game, false))
				.collect(Collectors.toList());
	}
}
