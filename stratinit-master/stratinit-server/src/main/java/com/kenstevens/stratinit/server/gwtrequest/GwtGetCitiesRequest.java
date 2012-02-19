package com.kenstevens.stratinit.server.gwtrequest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.client.gwt.model.GWTCity;
import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.server.gwtrequest.translate.GWTCityTranslate;
import com.kenstevens.stratinit.server.remote.helper.PlayerCityList;
import com.kenstevens.stratinit.server.remote.request.PlayerRequest;

@Scope("prototype")
@Component()
public class GwtGetCitiesRequest extends PlayerRequest<List<GWTCity>>  {
	@Autowired
	private PlayerCityList playerCityList;

	@Override
	protected List<GWTCity> execute() {
		List<SICity> result = playerCityList.getCities(getNation());
		return GWTCityTranslate.translate(result);
	}
}
