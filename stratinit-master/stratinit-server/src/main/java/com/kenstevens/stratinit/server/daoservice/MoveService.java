package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.List;

public interface MoveService {

    Result<MoveCost> move(Nation nation, List<SIUnit> units,
                          SectorCoords target);

}