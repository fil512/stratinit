package com.kenstevens.stratinit.server.remote.request;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;

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
