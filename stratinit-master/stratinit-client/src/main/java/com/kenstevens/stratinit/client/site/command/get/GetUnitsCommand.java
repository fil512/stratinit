package com.kenstevens.stratinit.client.site.command.get;

import com.kenstevens.stratinit.client.site.GetCommand;
import com.kenstevens.stratinit.client.site.processor.UnitListProcessor;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetUnitsCommand extends GetCommand<List<SIUnit>> {
	@Autowired
	private UnitListProcessor unitListProcessor;

	@Override
	public Result<List<SIUnit>> execute() {
        return stratInitServer.getUnits();
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
