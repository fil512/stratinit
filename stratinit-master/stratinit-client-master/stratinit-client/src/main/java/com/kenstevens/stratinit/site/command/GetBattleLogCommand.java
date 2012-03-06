package com.kenstevens.stratinit.site.command;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.BattleLogProcessor;

@Scope("prototype")
@Component
public class GetBattleLogCommand extends Command<List<SIBattleLog>> {
	@Autowired
	private BattleLogProcessor battleLogProcessor;

	@Override
	public Result<List<SIBattleLog>> execute() {
		return stratInit.getBattleLog();
	}

	@Override
	public void handleSuccess(List<SIBattleLog> silogs) {
		battleLogProcessor.process(silogs);
	}

	@Override
	public String getDescription() {
		return "Get battle log";
	}
}