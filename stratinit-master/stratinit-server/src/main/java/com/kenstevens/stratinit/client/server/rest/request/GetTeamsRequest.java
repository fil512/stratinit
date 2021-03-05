package com.kenstevens.stratinit.client.server.rest.request;

import com.kenstevens.stratinit.client.server.daoservice.GameDaoService;
import com.kenstevens.stratinit.dto.SITeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetTeamsRequest extends PlayerRequest<List<SITeam>> {
	@Autowired
	private GameDaoService gameDaoService;

	@Override
	protected List<SITeam> execute() {
		return gameDaoService.getTeams(getGame());
	}
}
