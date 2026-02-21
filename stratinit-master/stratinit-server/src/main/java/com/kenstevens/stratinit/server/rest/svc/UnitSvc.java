package com.kenstevens.stratinit.server.rest.svc;

import com.kenstevens.stratinit.client.model.MoveCost;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.dao.RelationDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.CityDaoService;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.rest.move.UnitCommandFactory;
import com.kenstevens.stratinit.server.rest.move.UnitsCede;
import com.kenstevens.stratinit.server.rest.request.WriteProcessor;
import com.kenstevens.stratinit.server.rest.request.write.TerrainSwitcher;
import com.kenstevens.stratinit.server.svc.MoveService;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UnitSvc {
	@Autowired
	private UnitDao unitDao;
	@Autowired
	private RelationDao relationDao;
	@Autowired
	private UnitDaoService unitDaoService;
	@Autowired
	private PlayerWorldViewUpdate playerWorldViewUpdate;
	@Autowired
	private UnitCommandFactory unitCommandFactory;
	@Autowired
	private SectorDaoService sectorDaoService;
	@Autowired
	private MoveService moveService;
	@Autowired
	private CityDaoService cityDaoService;
	@Autowired
	private TerrainSwitcher terrainSwitcher;

	public List<SIUnit> getUnits(final Nation nation) {
		List<Unit> units = unitDao.getUnits(nation);
		return unitsToSIUnits(nation, units);
	}

	private List<SIUnit> unitsToSIUnits(final Nation nation, Collection<Unit> units) {
		return units.stream()
				.map(unit -> unitToSIUnit(nation, unit))
				.collect(Collectors.toList());
	}

	public List<Unit> siunitToUnit(Collection<SIUnit> units) {
		return units.stream()
				.map(this::siunitToUnit)
				.collect(Collectors.toList());
	}

	private Unit siunitToUnit(SIUnit siunit) {
		return unitDao.findUnit(siunit.id);
	}

	private SIUnit unitToSIUnit(Nation nation, Unit unit) {
		SIUnit siunit = new SIUnit(unit);
		siunit.addPrivateData(nation, unit);
		return siunit;
	}

	public List<SIUnit> getSeenUnits(Nation nation) {
		Collection<Nation> allies = relationDao.getFriendsAndAllies(nation);
		Set<Unit> units = unitDaoService.getTeamSeenUnits(nation, allies);
		units.addAll(unitDaoService.getAllyUnits(allies));
		return unitsToSIUnits(nation, units);
	}

	public Result<SIUpdate> disbandUnits(Nation nation, List<SIUnit> siunits) {
		Result<None> result = Result.falseInstance();
		for (SIUnit siunit : siunits) {
			int unitId = siunit.id;
			Unit unit = unitDao.findUnit(unitId);
			if (unit == null) {
				result.or(new Result<>("Unit #" + unitId + " does not exist.", false));
			} else if (!unit.getNation().equals(nation)) {
				result.or(new Result<>("You do not own unit #" + unitId, false));
			} else {
				result.or(unitDaoService.disbandUnit(unit));
			}
		}
		SIUpdate siupdate = playerWorldViewUpdate.getWorldViewUpdate(nation);
		return new Result<>(result.getMessages(), true, siupdate, result.getBattleLogs(), result.isSuccess());
	}

	public Result<SIUpdate> cancelMoveOrders(Nation nation, List<SIUnit> siunits) {
		Result<None> result = Result.falseInstance();
		for (SIUnit siunit : siunits) {
			int unitId = siunit.id;
			Unit unit = unitDao.findUnit(unitId);
			if (unit == null) {
				result.or(new Result<>("Unit #" + unitId + " does not exist.", false));
			} else if (!unit.getNation().equals(nation)) {
				result.or(new Result<>("You do not own unit #" + unitId, false));
			} else if (unit.getUnitMove() == null) {
				result.or(new Result<>("Unit #" + unitId + " does not have a move order.", false));
			} else {
				result.or(unitDaoService.cancelMoveOrder(unit));
			}
		}
		SIUpdate siupdate = playerWorldViewUpdate.getWorldViewUpdate(nation);
		return new Result<>(result.getMessages(), true, siupdate, result.getBattleLogs(), result.isSuccess());
	}

	public Result<SIUpdate> cedeUnits(Nation nation, List<SIUnit> siunits, int nationId) {
		WorldView worldView = sectorDaoService.getAllWorldView(nation);
		UnitsCede unitCeder = unitCommandFactory.getSIUnitsCede(nation, siunits, nationId, worldView);
		return unitCeder.cede();
	}

	public Result<SIUpdate> buildCity(Nation nation, List<SIUnit> siunits) {
		return executeBuild(nation, siunits,
				com.kenstevens.stratinit.type.Constants.MOB_COST_TO_CREATE_CITY,
				unit -> cityDaoService.establishCity(unit));
	}

	public Result<SIUpdate> switchTerrain(Nation nation, List<SIUnit> siunits) {
		return executeBuild(nation, siunits,
				com.kenstevens.stratinit.type.Constants.MOB_COST_TO_SWITCH_TERRAIN,
				unit -> terrainSwitcher.switchTerrain(unit));
	}

	private Result<SIUpdate> executeBuild(Nation nation, List<SIUnit> siunits, int mobilityCost,
										  java.util.function.Function<Unit, Result<None>> buildAction) {
		Result<None> result = Result.falseInstance();
		boolean tried = false;
		List<Unit> units = siunitToUnit(siunits);
		for (Unit unit : units) {
			if (unit != null && unit.getType() == UnitType.ENGINEER && unit.getMobility() >= mobilityCost) {
				tried = true;
				Result<None> buildResult = buildUnit(nation, unit, mobilityCost, buildAction);
				result.or(buildResult);
				if (result.isSuccess()) {
					break;
				}
			}
		}
		if (!tried) {
			List<String> unitsSelected = new ArrayList<>();
			for (Unit unit : units) {
				if (unit != null) {
					unitsSelected.add(unit.getType() + " " + unit.getMobility() + " mob");
				}
			}
			String unitsSelectedString = StringUtils.join(unitsSelected, ", ");
			result.setMessage("No engineer selected with at least " + mobilityCost + " mobility.  Units Selected: [" + unitsSelectedString + "]");
		}
		SIUpdate siupdate = playerWorldViewUpdate.getWorldViewUpdate(nation);
		return new Result<>(result.getMessages(), result.isSuccess(), siupdate);
	}

	private Result<None> buildUnit(Nation nation, Unit unit, int mobilityCost,
								   java.util.function.Function<Unit, Result<None>> buildAction) {
		if (unit == null) {
			return new Result<>("Unit does not exist.", false);
		}
		int unitId = unit.getId();
		if (!unit.getNation().equals(nation)) {
			return new Result<>("You do not own unit #" + unitId, false);
		}
		if (!unit.isEngineer()) {
			return new Result<>("Only engineers may build a new city.  Unit #" + unitId + " is a " + unit.getType(), false);
		}
		if (unit.getMobility() < mobilityCost) {
			return new Result<>("An engineer must have " + mobilityCost + " to build a new city.  Engineer #" + unitId + " only has " + unit.getMobility(), false);
		}
		return buildAction.apply(unit);
	}

	public WriteProcessor.CommandResult<SIUpdate> moveUnits(Nation nation, List<SIUnit> units, SectorCoords target) {
		Result<MoveCost> result = moveService.move(nation, units, target);
		int commandCost = com.kenstevens.stratinit.type.Constants.COMMAND_COST;
		MoveCost moveCost = result.getValue();
		if (moveCost != null) {
			commandCost = moveCost.getCommandCost();
		}
		SIUpdate siupdate = playerWorldViewUpdate.getWorldViewUpdate(nation);
		Result<SIUpdate> updateResult = new Result<>(result.getMessages(), true, siupdate, result.getBattleLogs(), result.isSuccess());
		return new WriteProcessor.CommandResult<>(updateResult, commandCost);
	}
}
