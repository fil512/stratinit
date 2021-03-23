package com.kenstevens.stratinit.client.site.command.get;

import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.site.GetCommand;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetMyNationCommand extends GetCommand<SINation> {
	@Autowired
	private Data db;

	@Override
	public Result<SINation> execute() {
        return stratInitServer.getMyNation();
    }

	@Override
	public String getDescription() {
		return "Get my nation";
	}

	@Override
	public void handleSuccess(SINation sination) {
		db.setLastLoginTime(sination.lastAction);
	}
}
