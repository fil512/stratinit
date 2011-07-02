package com.kenstevens.stratinit.server.remote.move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.remote.helper.PlayerWorldViewUpdate;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;

@Scope("prototype")
@Component
public class UnitsCede {
	@Autowired
	private GameDao gameDao;
	@Autowired
	private PlayerWorldViewUpdate playerWorldViewUpdate;
	@Autowired
	private UnitDaoService unitDaoService;
	@Autowired
	private SectorDaoService sectorDaoService;
	@Autowired
	private UnitDao unitDao;

	private final Nation nation;
	private final List<Unit> units;
	private final int nationId;
	private final WorldView worldView;

	public UnitsCede(Nation nation, List<Unit> units, int nationId,
			WorldView worldView) {
		this.nation = nation;
		this.units = units;
		this.nationId = nationId;
		this.worldView = worldView;
	}

	public Result<SIUpdate> cede() {
		if (units.isEmpty()) {
			return new Result<SIUpdate>("Nothing to cede.", false);
		}

		int gameId = nation.getGameId();

		List<Unit> unitsToCede = new ArrayList<Unit>();
		Nation target = gameDao.getNation(gameId, nationId);
		
		Relation relation = gameDao.findRelation(nation, target);
		Relation reverse = gameDao.findRelation(target, nation);
		if (relation.getType() != RelationType.ALLIED || reverse.getType() != RelationType.ALLIED) {
			return new Result<SIUpdate>("You may only cede units to an ally.", false);
		}

		SectorCoords coords = units.get(0).getCoords();
		WorldSector worldSector = worldView.getWorldSector(coords);

		for (Unit unit : units) {
			// Exclude units not in the same sector as the first unit
			if (!unit.getCoords().equals(coords)) {
				continue;
			}
			unitsToCede.add(unit);
			if (unit.carriesUnits()) {
				unitsToCede.addAll(unitDaoService.getPassengers(unit,
						worldSector));

			}
		}

		if (unitsToCede.isEmpty()) {
			return new Result<SIUpdate>("Nothing to cede.", false);
		}

		Collection<Nation> allies = gameDao.getAllies(nation);
		if (!allies.contains(target)
				&& extraUnitsInSector(nation, coords, unitsToCede)) {
			return new Result<SIUpdate>("Unable to cede units: You have other units in the same sector.", false);
		}

		Result<None> result = Result.trueInstance();
		for (Unit unitToCede : unitsToCede) {
			result.or(unitDaoService.cedeUnit(unitToCede, target));
		}
		sectorDaoService.survey(nation);
		sectorDaoService.survey(target);
		SIUpdate siupdate = playerWorldViewUpdate.getWorldViewUpdate(nation);

		return new Result<SIUpdate>(result.getMessages(), true, siupdate,
				result.getBattleLogs(), result.isSuccess());
	}

	private boolean extraUnitsInSector(Nation nation, SectorCoords coords,
			List<Unit> unitsToCede) {

		Collection<Unit> unitsInSector = unitDao.getUnits(nation.getGame(), coords);

		boolean extraUnitsInSector = false;
		for (Unit sectorUnit : unitsInSector) {
			if (!unitsToCede.contains(sectorUnit)) {
				extraUnitsInSector = true;
			}
		}
		return extraUnitsInSector;
	}

}
