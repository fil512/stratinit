package com.kenstevens.stratinit.server.gwtrequest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.client.gwt.model.GWTUnit;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.server.gwtrequest.translate.GWTUnitTranslate;
import com.kenstevens.stratinit.server.remote.helper.PlayerUnitList;
import com.kenstevens.stratinit.server.remote.request.PlayerRequest;

@Scope("prototype")
@Component()
public class GwtGetUnitsRequest extends PlayerRequest<List<GWTUnit>>  {
	@Autowired
	private PlayerUnitList playerUnitList;

	@Override
	protected List<GWTUnit> execute() {
		List<SIUnit> result = playerUnitList.getUnits(getNation());
		return GWTUnitTranslate.translate(result);
	}
}
