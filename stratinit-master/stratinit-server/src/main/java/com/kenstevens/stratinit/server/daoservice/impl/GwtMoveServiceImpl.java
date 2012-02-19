package com.kenstevens.stratinit.server.daoservice.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.client.gwt.model.GWTUnit;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.GwtMoveService;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.gwtrequest.translate.GWTUnitTranslate;
import com.kenstevens.stratinit.server.remote.move.UnitCommandFactory;
import com.kenstevens.stratinit.server.remote.move.UnitsMove;
import com.kenstevens.stratinit.type.SectorCoords;

@Service
public class GwtMoveServiceImpl implements GwtMoveService {
	@Autowired
	private UnitCommandFactory unitCommandFactory;
	@Autowired
	private SectorDaoService sectorDaoService;
	@Override
	public Result<None> move(Nation nation, List<GWTUnit> units,
			SectorCoords target) {
		WorldView worldView = sectorDaoService.getAllWorldView(nation);
		UnitsMove unitMover = unitCommandFactory.getSIUnitsMove(nation,
				GWTUnitTranslate.inTranslate(units), target, worldView);
		return new Result<None>(unitMover.move());
	}

}
