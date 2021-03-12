package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.client.model.MoveCost;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.rest.move.UnitCommandFactory;
import com.kenstevens.stratinit.server.rest.move.UnitsMove;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoveService {
    @Autowired
    private UnitCommandFactory unitCommandFactory;
    @Autowired
    private SectorDaoService sectorDaoService;

    public Result<MoveCost> move(Nation nation, List<SIUnit> units,
                                 SectorCoords target) {
        WorldView worldView = sectorDaoService.getAllWorldView(nation);
        UnitsMove unitMover = unitCommandFactory.getSIUnitsMove(nation,
                units, target, worldView);
        return unitMover.move();
    }
}