package com.kenstevens.stratinit.client.site.action.get;

import com.kenstevens.stratinit.client.model.BattleLogList;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.shell.StatusReporter;
import com.kenstevens.stratinit.client.site.GetAction;
import com.kenstevens.stratinit.client.site.command.get.GetBattleLogCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class BattleLogAction extends GetAction<GetBattleLogCommand> {
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private Data db;

	protected BattleLogAction() {
		super(new GetBattleLogCommand());
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
}