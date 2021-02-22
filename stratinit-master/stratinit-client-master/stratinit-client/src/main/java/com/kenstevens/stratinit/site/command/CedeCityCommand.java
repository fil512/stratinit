package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class CedeCityCommand extends CedeCommand {
	private final City city;

	public CedeCityCommand(City city, Nation nation) {
		super(nation);
		this.city = city;
	}

	@Override
	public Result<SIUpdate> execute() {
		return stratInit.cedeCity(new SICity(city), nation.getNationId());
	}

	@Override
	public String getDescription() {
		return "Give city and units at "+city.getCoords()+" to "+nation;
	}
}
