package com.kenstevens.stratinit.server.daoserviceimpl;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.MoveService;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.remote.move.UnitCommandFactory;
import com.kenstevens.stratinit.server.remote.move.UnitsMove;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoveServiceImpl implements MoveService {
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
