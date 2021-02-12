package com.kenstevens.stratinit.server.remote.request.write;

import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.World;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class TerrainSwitcher {
	@Autowired
	private UnitDaoService unitDaoService;
	@Autowired
	private SectorDaoService sectorDaoService;
	@Autowired
	private SectorDao sectorDao;
	@Autowired
	private UnitDao unitDao;

	public Result<None> switchTerrain(Unit unit) {
		World world = sectorDao.getWorld(unit.getGame());
		Sector sector = world.getSector(unit);
		if (nonEngineerUnitsInSector(sector)) {
			return new Result<None>("Only Engineer units may be in a sector to change its terrain", false);
		}
		SectorType fromType = sector.getType();
		SectorType toType;
		if (fromType == SectorType.LAND) {
			toType = SectorType.WATER;
			if (!hasNeighbourOfType(world, unit.getCoords(), toType)) {
				return new Result<None>("Can only switch LAND to WATER if there is already WATER adjacent.", false);
			}
		} else if (fromType == SectorType.WATER) {
			toType = SectorType.LAND;
			if (!hasNeighbourOfType(world, unit.getCoords(), toType)) { 
				return new Result<None>("Can only switch WATER to LAND if there is already LAND adjacent.", false);
			}
		} else {
			return new Result<None>("Only LAND and WATER terrain can be changed.", false);
		}
		
		sector.setType(toType);

		sectorDaoService.merge(sector);
		unit.decreaseMobility(Constants.MOB_COST_TO_SWITCH_TERRAIN);
		unitDaoService.merge(unit);
		return new Result<None>("Terrain switched from "+fromType+" to "+toType, true);
	}

	private boolean nonEngineerUnitsInSector(Sector sector) {
		Collection<Unit> units = unitDao.getUnits(sector);
		for (Unit unit : units) {
			if (unit.getType() != UnitType.ENGINEER) {
				return true;
			}
		}
		return false;
	}

	private boolean hasNeighbourOfType(World world, SectorCoords coords,
			SectorType toType) {
		for (Sector neighbour : world.getNeighbours(coords)) {
			if (typeMatches(toType, neighbour.getType())) {
				return true;
			}
		}
		return false;
	}

	private boolean typeMatches(SectorType toType, SectorType sectorType) {
		if (toType == SectorType.WATER) {
			return sectorType == SectorType.WATER;
		} else if (toType == SectorType.LAND) {
			return sectorType == SectorType.LAND || sectorType == SectorType.PLAYER_CITY;
		} else {
			return false;
		}
	}

}
