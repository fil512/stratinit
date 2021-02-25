package com.kenstevens.stratinit.server.remote.request;

import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.server.remote.helper.PlayerNationList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetJoinedGamesRequest extends PlayerRequest<List<SIGame>> {
	@Autowired
	private PlayerNationList playerNationList;

	@Override
	protected List<SIGame> execute() {
		return playerNationList.getJoinedGames(getPlayer());
	}
}
