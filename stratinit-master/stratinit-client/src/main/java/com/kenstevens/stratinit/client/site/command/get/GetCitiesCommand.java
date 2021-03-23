package com.kenstevens.stratinit.client.site.command.get;

import com.kenstevens.stratinit.client.site.GetCommand;
import com.kenstevens.stratinit.client.site.processor.CityListProcessor;
import com.kenstevens.stratinit.dto.SICityUpdate;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetCitiesCommand extends GetCommand<List<SICityUpdate>> {
	@Autowired
	private CityListProcessor cityListProcessor;

	@Override
	public Result<List<SICityUpdate>> execute() {
		return stratInitServer.getCities();
	}

	@Override
	public void handleSuccess(List<SICityUpdate> sicities) {
		cityListProcessor.process(sicities);
	}

	@Override
	public String getDescription() {
		return "Get city list";
	}
}
