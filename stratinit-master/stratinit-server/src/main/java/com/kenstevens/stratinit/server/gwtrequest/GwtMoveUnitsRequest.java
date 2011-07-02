package com.kenstevens.stratinit.server.gwtrequest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.client.gwt.model.GWTSectorCoords;
import com.kenstevens.stratinit.client.gwt.model.GWTUnit;
import com.kenstevens.stratinit.client.gwt.model.GWTUpdate;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.GwtMoveService;
import com.kenstevens.stratinit.server.remote.request.write.PlayerWriteRequest;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;

@Scope("prototype")
@Component()
public class GwtMoveUnitsRequest extends PlayerWriteRequest<GWTUpdate>  {

	@Autowired
	private GwtMoveService gwtMoveService;
	@Autowired
	private GwtUpdater gwtUpdater;
	private final List<GWTUnit> units;
	private final GWTSectorCoords coords;

	public GwtMoveUnitsRequest(List<GWTUnit> units, GWTSectorCoords coords) {
		this.units = units;
		this.coords = coords;
	}

	@Override
	protected Result<GWTUpdate> executeWrite() {
		Nation nation = getNation();
		SectorCoords target = new SectorCoords(coords.x, coords.y);
		Result<None> result = gwtMoveService.move(nation, units, target);

		return new Result<GWTUpdate>(result.getMessages(), result.isSuccess(),
				gwtUpdater.getUpdate(getNation()), result.getBattleLogs(), result.isSuccess());
	}

	@Override
	protected int getCommandCost() {
		return Constants.COMMAND_COST;
	}
}
