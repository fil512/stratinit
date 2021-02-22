package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.SeenUnitListProcessor;
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
		return stratInit.getSeenUnits();
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
