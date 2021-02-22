package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetMyNationCommand extends Command<SINation> {
	@Autowired
	private Data db;

	@Override
	public Result<SINation> execute() {
		return stratInit.getMyNation();
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
