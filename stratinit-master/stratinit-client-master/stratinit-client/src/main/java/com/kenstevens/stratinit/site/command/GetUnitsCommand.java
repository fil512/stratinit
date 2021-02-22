package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.UnitListProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetUnitsCommand extends Command<List<SIUnit>> {
	@Autowired
	private UnitListProcessor unitListProcessor;

	@Override
	public Result<List<SIUnit>> execute() {
		return stratInit.getUnits();
	}

	@Override
	public String getDescription() {
		return "Get Unit list";
	}

	@Override
	public void handleSuccess(List<SIUnit> siunits) {
		unitListProcessor.process(siunits, false);
	}
}
