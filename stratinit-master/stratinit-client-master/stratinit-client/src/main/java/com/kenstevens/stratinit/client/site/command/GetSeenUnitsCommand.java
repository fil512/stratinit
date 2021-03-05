package com.kenstevens.stratinit.client.site.command;

import com.kenstevens.stratinit.client.site.Command;
import com.kenstevens.stratinit.client.site.processor.SeenUnitListProcessor;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetSeenUnitsCommand extends Command<List<SIUnit>> {
	@Autowired
	private SeenUnitListProcessor seenUnitListProcessor;

	@Override
	public Result<List<SIUnit>> execute() {
        return stratInitServer.getSeenUnits();
    }

	@Override
	public String getDescription() {
		return "Get Seen Units";
	}

	@Override
	public void handleSuccess(List<SIUnit> siunits) {
		seenUnitListProcessor.process(siunits);
	}
}
