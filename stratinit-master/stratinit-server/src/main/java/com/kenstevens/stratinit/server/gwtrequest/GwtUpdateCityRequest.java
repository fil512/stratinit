package com.kenstevens.stratinit.server.gwtrequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.client.gwt.model.GWTCity;
import com.kenstevens.stratinit.client.gwt.model.GWTUpdate;
import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.server.gwtrequest.translate.GWTCityTranslate;
import com.kenstevens.stratinit.server.remote.helper.CityUpdater;
import com.kenstevens.stratinit.server.remote.request.write.PlayerWriteRequest;
import com.kenstevens.stratinit.type.Constants;

@Scope("prototype")
@Component()
public class GwtUpdateCityRequest extends PlayerWriteRequest<GWTUpdate> {
	@Autowired
	private CityUpdater cityUpdater;
	@Autowired
	private GwtUpdater gwtUpdater;

	private final GWTCity city;
	private final boolean isBuild;

	public GwtUpdateCityRequest(GWTCity city, boolean isBuild) {
		this.city = city;
		this.isBuild = isBuild;
	}

	@Override
	protected Result<GWTUpdate> executeWrite() {
		Nation nation = getNation();
		SICity sicity = GWTCityTranslate.inTranslate(city);

		Result<SICity> result = cityUpdater.updateCity(nation, sicity,
				isBuild ? UpdateCityField.BUILD : UpdateCityField.NEXT_BUILD);

		return new Result<GWTUpdate>(result.getMessages(), result.isSuccess(),
				gwtUpdater.getUpdate(nation), result.getBattleLogs(), result.isSuccess());
	}

	@Override
	protected int getCommandCost() {
		return Constants.COMMAND_COST;
	}
}
