package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.model.BattleLogList;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.GetBattleLogCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class BattleLogAction extends Action<GetBattleLogCommand> {
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private Data db;

	protected GetBattleLogCommand buildCommand() {
		return new GetBattleLogCommand();
	}

	@Override
	public void postRequest() {
		BattleLogList battleLogList = db.getBattleLogList();
		if (battleLogList == null) {
			statusReporter.reportResult("No battle log entries loaded.");
		} else {
			statusReporter.reportResult(battleLogList.size()
					+ " battle log entries loaded.");
		}
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}