package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.BattleLogList;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.GetBattleLogCommand;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class BattleLogAction extends Action {
	@Autowired
	private Spring spring;
	private GetBattleLogCommand getBattleLogCommand;
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private Data db;

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		getBattleLogCommand = spring.getBean(GetBattleLogCommand.class);
	}

	@Override
	public Command<? extends Object> getCommand() {
		return getBattleLogCommand;
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