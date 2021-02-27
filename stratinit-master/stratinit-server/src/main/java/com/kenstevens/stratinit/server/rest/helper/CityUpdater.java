package com.kenstevens.stratinit.server.rest.helper;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityUpdater {
	@Autowired
	private SectorDaoService sectorDaoService;
	@Autowired
	private PlayerCityList playerCityList;
	@Autowired
	private DataCache dataCache;


	public Result<SICity> updateCity(Nation nation, SICity sicity, UpdateCityField field) {
		SectorCoords coords = sicity.coords;

		if (field == UpdateCityField.BUILD) {
			Result<SICity> retval = checkBuild(nation, sicity, coords);
			if (!retval.isSuccess()) {
				return retval;
			}
		} else if (field == UpdateCityField.NEXT_BUILD) {
			Result<SICity> retval = checkNextBuild(nation, sicity, coords);
			if (!retval.isSuccess()) {
				return retval;
			}
		}
		Result<City> cityResult = updateCity(nation, field, sicity);

		return new Result<SICity>(cityResult.getMessages(), true,
				playerCityList.cityToSICity(nation, cityResult.getValue()));
	}

	private Result<SICity> checkBuild(Nation nation, SICity sicity, SectorCoords coords) {
		UnitType build = sicity.build;
		if (!sufficientTech(nation, build, 0)) {
			return new Result<SICity>("Insufficient tech to build " + build,
					false);
		}
		if (isNaval(build) && !canDesignatePort(nation.getGameId(), coords)) {
			return new Result<SICity>("There is no water adjacent to "
					+ coords + ".  You can not build ships there.", false);
		}
		return new Result<SICity>(sicity);
	}

	private Result<SICity> checkNextBuild(Nation nation, SICity sicity, SectorCoords coords) {
		UnitType nextBuild = sicity.nextBuild;
		if (!sufficientTech(nation, nextBuild, Constants.TECH_NEXT_BUILD)) {
			return new Result<SICity>(
					"Insufficient tech to build " + nextBuild, false);
		}
		if (isNaval(nextBuild) && !canDesignatePort(nation.getGameId(), coords)) {
			return new Result<SICity>("There is no water adjacent to "
					+ coords + ".  You can not build ships there.", false);
		}
		return new Result<SICity>(sicity);
	}

	private Result<City> updateCity(Nation nation, UpdateCityField field,
									SICity sicity) {
		return sectorDaoService.updateCity(nation, sicity.coords, field,
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
