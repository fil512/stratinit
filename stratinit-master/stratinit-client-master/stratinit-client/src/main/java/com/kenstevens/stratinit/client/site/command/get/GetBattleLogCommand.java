package com.kenstevens.stratinit.client.site.command.get;

import com.kenstevens.stratinit.client.site.GetCommand;
import com.kenstevens.stratinit.client.site.processor.BattleLogProcessor;
import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetBattleLogCommand extends GetCommand<List<SIBattleLog>> {
	@Autowired
	private BattleLogProcessor battleLogProcessor;

	@Override
	public Result<List<SIBattleLog>> execute() {
        return stratInitServer.getBattleLog();
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
