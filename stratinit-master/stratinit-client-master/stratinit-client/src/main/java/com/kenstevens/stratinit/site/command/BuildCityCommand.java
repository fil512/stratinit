package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.SIUnitListJson;
import com.kenstevens.stratinit.site.PostCommand;
import com.kenstevens.stratinit.site.processor.UpdateProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class BuildCityCommand extends PostCommand<SIUpdate, SIUnitListJson> {
	@Autowired
	private UpdateProcessor updateProcessor;

	public BuildCityCommand(SIUnitListJson request) {
		super(request);
	}

	@Override
	public Result<SIUpdate> execute() {
		return stratInitServer.buildCity(getRequest().siunits);
	}

	@Override
	public void handleSuccess(SIUpdate siupdate) {
		updateProcessor.process(siupdate, false);
	}

	@Override
	public String getDescription() {
		SIUnitListJson request = getRequest();
		if (request.siunits.size() == 0) {
			return "No unit selected to build with.";
		} else {
			return "New city built at " + request.siunits.get(0).coords;
		}
	}
}
