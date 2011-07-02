package com.kenstevens.stratinit.server.remote.request.write;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.remote.move.UnitCommandFactory;
import com.kenstevens.stratinit.server.remote.move.UnitsCede;
import com.kenstevens.stratinit.type.Constants;

@Scope("prototype")
@Component
public class CedeUnitsRequest extends PlayerWriteRequest<SIUpdate> {

	@Autowired
	private SectorDaoService sectorDaoService;

	@Autowired
	private UnitCommandFactory unitCommandFactory;

	private final List<SIUnit> siunits;
	private final int nationId;

	public CedeUnitsRequest(List<SIUnit> siunits, int nationId) {
		this.siunits = siunits;
		this.nationId = nationId;
	}

	@Override
	protected Result<SIUpdate> executeWrite() {
		Nation nation = getNation();
		WorldView worldView = sectorDaoService.getAllWorldView(nation);
		UnitsCede unitCeder = unitCommandFactory.getSIUnitsCede(nation,
				siunits, nationId, worldView);
		return unitCeder.cede();
	}

	@Override
	protected int getCommandCost() {
		return Constants.COMMAND_COST;
	}

}
