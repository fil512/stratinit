package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.remote.SIResponseEntity;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.BattleLogProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetBattleLogCommand extends Command<List<SIBattleLog>> {
	@Autowired
	private BattleLogProcessor battleLogProcessor;

	@Override
	public SIResponseEntity<List<SIBattleLog>> execute() {
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
