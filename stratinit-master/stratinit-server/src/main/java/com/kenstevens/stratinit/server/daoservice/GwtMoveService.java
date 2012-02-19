package com.kenstevens.stratinit.server.daoservice;

import java.util.List;

import com.kenstevens.stratinit.client.gwt.model.GWTUnit;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.SectorCoords;

public interface GwtMoveService {
	public abstract Result<None> move(Nation nation, List<GWTUnit> units,
			SectorCoords target);
}
