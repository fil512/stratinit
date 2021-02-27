package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.UnitListJson;
import com.kenstevens.stratinit.site.PostCommand;
import com.kenstevens.stratinit.site.processor.UpdateProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class BuildCityCommand extends PostCommand<SIUpdate, UnitListJson> {
	@Autowired
	private UpdateProcessor updateProcessor;

	public BuildCityCommand(UnitListJson request) {
		super(request);
	}

	@Override
	public Result<SIUpdate> execute() {
		List<SIUnit> siunits = UnitsToSIUnits.transform(getRequest());
		return stratInitServer.buildCity(siunits);
	}

	@Override
	public void handleSuccess(SIUpdate siupdate) {
		updateProcessor.process(siupdate, false);
	}

	@Override
	public String getDescription() {
		UnitListJson request = getRequest();
		if (request.units.size() == 0) {
			return "No unit selected to build with.";
		} else {
			return "New city built at " + request.units.get(0).getCoords();
		}
	}
}
