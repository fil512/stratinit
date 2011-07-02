package com.kenstevens.stratinit.server.daoservice;

import java.util.List;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.move.cost.MoveCost;
import com.kenstevens.stratinit.type.SectorCoords;

public interface MoveService {

	public abstract Result<MoveCost> move(Nation nation, List<SIUnit> units,
			SectorCoords target);

}