package com.kenstevens.stratinit.server.rest.request;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetUnjoinedGamesRequest extends PlayerRequest<List<SIGame>> {
	@Autowired
	private GameDaoService gameDaoService;

	@Override
	protected List<SIGame> execute() {
		return Lists.newArrayList(Collections2.transform(gameDaoService.getUnjoinedGames(getPlayer()), new Function<Game, SIGame>() {
			public SIGame apply(Game game) {
				return new SIGame(game, false);
			}
		}));
	}
}
