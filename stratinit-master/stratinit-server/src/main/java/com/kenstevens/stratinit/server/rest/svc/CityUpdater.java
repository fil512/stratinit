package com.kenstevens.stratinit.server.rest.svc;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.model.UnitBase;
import com.kenstevens.stratinit.dto.SICityUpdate;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.CityDaoService;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityUpdater {
	@Autowired
	private CityDaoService cityDaoService;
	@Autowired
	private CitySvc citySvc;
	@Autowired
	private DataCache dataCache;


	public Result<SICityUpdate> updateCity(Nation nation, SICityUpdate sicity, CityFieldToUpdateEnum field) {
		SectorCoords coords = sicity.coords;

		if (field == CityFieldToUpdateEnum.BUILD) {
			Result<SICityUpdate> retval = checkBuild(nation, sicity, coords);
			if (!retval.isSuccess()) {
				return retval;
			}
		} else if (field == CityFieldToUpdateEnum.NEXT_BUILD) {
			Result<SICityUpdate> retval = checkNextBuild(nation, sicity, coords);
			if (!retval.isSuccess()) {
				return retval;
			}
		}
		Result<City> cityResult = updateCity(nation, field, sicity);

		return new Result<>(cityResult.getMessages(), true,
				citySvc.cityToSICity(nation, cityResult.getValue(), field));
	}

	private Result<SICityUpdate> checkBuild(Nation nation, SICityUpdate sicity, SectorCoords coords) {
		UnitType build = sicity.build;
		if (!sufficientTech(nation, build, 0)) {
			return new Result<SICityUpdate>("Insufficient tech to build " + build,
					false);
		}
		if (isNaval(build) && !canDesignatePort(nation.getGameId(), coords)) {
			return new Result<SICityUpdate>("There is no water adjacent to "
					+ coords + ".  You can not build ships there.", false);
		}
		return new Result<SICityUpdate>(sicity);
	}

	private Result<SICityUpdate> checkNextBuild(Nation nation, SICityUpdate sicity, SectorCoords coords) {
		UnitType nextBuild = sicity.nextBuild;
		if (!sufficientTech(nation, nextBuild, Constants.TECH_NEXT_BUILD)) {
			return new Result<SICityUpdate>(
					"Insufficient tech to build " + nextBuild, false);
		}
		if (isNaval(nextBuild) && !canDesignatePort(nation.getGameId(), coords)) {
			return new Result<SICityUpdate>("There is no water adjacent to "
					+ coords + ".  You can not build ships there.", false);
		}
		return new Result<SICityUpdate>(sicity);
	}

	private Result<City> updateCity(Nation nation, CityFieldToUpdateEnum field,
									SICityUpdate sicity) {
		return cityDaoService.updateCity(nation, sicity.coords, field,
				sicity.build, sicity.nextBuild, sicity.switchOnTechChange, sicity.nextCoords);
	}

	private boolean sufficientTech(Nation nation, UnitType unitType,
								   int techNextBuild) {
		return unitType == null
				|| nation.getTech() + techNextBuild >= UnitBase.getUnitBase(
				unitType).getTech();
	}

	private boolean canDesignatePort(int gameId, SectorCoords coords) {
		List<Sector> neighbours = dataCache.getWorld(gameId).getNeighbours(
				coords);
		for (Sector neighbour : neighbours) {
			if (neighbour == null) {
				continue;
			}
			if (neighbour.isWater()) {
				return true;
			}
		}
		return false;
	}

	private boolean isNaval(UnitType unitType) {
		return unitType != null && UnitBase.getUnitBase(unitType).isNavy();
	}
}
