package com.kenstevens.stratinit.server.remote.request.write;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.type.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class SwitchTerrainRequest extends BuildRequest {
	@Autowired
	SectorDaoService sectorDaoService;
	@Autowired
	TerrainSwitcher terrainSwitcher;

	public SwitchTerrainRequest(List<SIUnit> siunits) {
		super(siunits);
	}

	@Override
	protected int mobilityCost() {
		return Constants.MOB_COST_TO_SWITCH_TERRAIN;
	}


	@Override
	protected int getCommandCost() {
		return Constants.COMMAND_COST_SWITCH_TERRAIN;
	}

	@Override
	protected Result<None> buildIt(Unit unit) {
		return terrainSwitcher.switchTerrain(unit);
	}


}
