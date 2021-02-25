package com.kenstevens.stratinit.server.remote.move;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.model.AttackType;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.Attack;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.util.AttackHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Scope("prototype")
@Component
public class Interception {
	private final Nation enemyNation;
	private final SectorCoords targetCoords;
	private final int numberOfTargetsUnits;
	private final Nation actor;
	@Autowired
	private UnitDao unitDao;
	@Autowired
	private UnitDaoService unitDaoService;
	@Autowired
	private GameDao gameDao;
	@Autowired
	private SectorDao sectorDao;
	@Autowired
	private SectorDaoService sectorDaoService;
	@Autowired
	private UnitCommandFactory unitCommandFactory;

	public Interception(UnitsToMove unitsToMove, SectorCoords targetCoords) {
		this.actor = unitsToMove.getActor();
		this.enemyNation = unitsToMove.getNation();
		this.targetCoords = targetCoords;
		this.numberOfTargetsUnits = unitsToMove.getNumberOfUnits();
	}

	/**
	 * @return success means no interception
	 */
	public Result<None> intercept() {
		Result<None> retval = Result.trueInstance();
		Collection<Nation> nations = sectorDao
				.getOtherNationsThatSeeThisSector(enemyNation, targetCoords);
		Map<Nation, RelationType> theirRelations = gameDao
				.getTheirRelationTypesAsMap(enemyNation);
		for (Nation nation : nations) {
			if (!AttackHelper.canAttack(AttackType.INTERCEPTION,
					theirRelations.get(nation))) {
				continue;
			}
			WorldView worldView = sectorDaoService.getInterceptionWorldView(
					targetCoords, nation);
			Collection<Unit> units = unitDao.getUnitsWithin(worldView, nation,
					targetCoords, Constants.INTERCEPTION_RADIUS);
			WorldSector targetSector = worldView.getWorldSector(targetCoords);
			Interceptors interceptors = new Interceptors();
			for (Unit unit : units) {
				Attack attack = new Attack(targetSector);
				if (!attack.canAttack(worldView, AttackType.INTERCEPTION, unit)) {
					continue;
				}
				if (unit.getAmmo() <= 0) {
					continue;
				}
				if (unit.isHurt()) {
					continue;
				}
				interceptors.add(unit);
				if (interceptors.size() >= numberOfTargetsUnits) {
					break;
				}
			}
			if (interceptors.isEmpty()) {
				continue;
			}
			for (List<Unit> fighters : interceptors.unitsBySector()) {
				UnitsToMove interceptorsToMove = new UnitsToMove(actor, AttackType.INTERCEPTION, nation, fighters,
						targetCoords);
				UnitsMove unitsMove = unitCommandFactory.getUnitsMove(
						interceptorsToMove, worldView);
				Result<None> interceptResult = new Result<None>(unitsMove.move());
				interceptResult.setSuccess(!interceptResult.isSuccess());
				retval.and(interceptResult);
			}
			// success for my enemy is failure for me
			interceptors.flyBack(unitDaoService, worldView);
		}
		return retval;
	}

}
