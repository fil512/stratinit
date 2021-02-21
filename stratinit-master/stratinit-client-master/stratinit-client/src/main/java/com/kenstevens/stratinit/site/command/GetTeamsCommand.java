package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.event.TeamListArrivedEvent;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.remote.SIResponseEntity;
import com.kenstevens.stratinit.site.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetTeamsCommand extends Command<List<SITeam>> {
	@Autowired
	private Data db;

	@Override
	public SIResponseEntity<List<SITeam>> execute() {
		return stratInit.getTeams();
	}

	@Override
	public String getDescription() {
		return "Get teams";
	}

	@Override
	public void handleSuccess(List<SITeam> siteams) {
		db.getTeamList().addAll(siteams);
		arrivedDataEventAccumulator.addEvent(new TeamListArrivedEvent());
	}
}
