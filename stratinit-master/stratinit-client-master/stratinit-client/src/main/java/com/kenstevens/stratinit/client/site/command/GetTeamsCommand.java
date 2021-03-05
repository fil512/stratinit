package com.kenstevens.stratinit.client.site.command;

import com.kenstevens.stratinit.client.event.TeamListArrivedEvent;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.site.Command;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.remote.Result;
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
	public Result<List<SITeam>> execute() {
        return stratInitServer.getTeams();
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
