package com.kenstevens.stratinit.client.site.command.post;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.site.command.get.CedeCommand;
import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.CedeCityJson;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class CedeCityCommand extends CedeCommand<CedeCityJson> {
	public CedeCityCommand(City city, Nation nation) {
		super(new CedeCityJson(new SICity(city), nation.getNationId()), buildDescription(city, nation));
	}

	@Override
	public Result<SIUpdate> executePost(CedeCityJson request) {
		return stratInitServer.cedeCity(request);
	}

	public static String buildDescription(City city, Nation nation) {
		return "Give city and units at "+city.getCoords()+" to "+nation;
	}
}
