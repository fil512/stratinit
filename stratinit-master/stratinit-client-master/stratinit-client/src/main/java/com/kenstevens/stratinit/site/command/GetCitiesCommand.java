package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.CityListProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetCitiesCommand extends Command<List<SICity>> {
	@Autowired
	private CityListProcessor cityListProcessor;

	@Override
	public Result<List<SICity>> execute() {
        return stratInitServer.getCities();
    }

	@Override
	public void handleSuccess(List<SICity> sicities) {
		cityListProcessor.process(sicities);
	}

	@Override
	public String getDescription() {
		return "Get city list";
	}
}
