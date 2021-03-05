package com.kenstevens.stratinit.client.server.rest.request;

import com.kenstevens.stratinit.client.server.rest.svc.NationSvc;
import com.kenstevens.stratinit.dto.SIGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetJoinedGamesRequest extends PlayerRequest<List<SIGame>> {
	@Autowired
	private NationSvc nationSvc;

	@Override
	protected List<SIGame> execute() {
		return nationSvc.getJoinedGames(getPlayer());
	}
}
