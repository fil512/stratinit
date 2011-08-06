package com.kenstevens.stratinit.server.remote.move;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.main.Spring;
import com.kenstevens.stratinit.model.AttackType;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.SectorSeen;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.Attack;
import com.kenstevens.stratinit.move.Movement;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.MoveSeen;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.remote.move.cost.MoveCost;
import com.kenstevens.stratinit.server.remote.move.cost.MoveType;
import com.kenstevens.stratinit.type.SectorCoords;

@Scope("prototype")
@Component
public class UnitsMove {

	@Autowired
	private UnitCommandFactory unitCommandFactory;
	@Autowired
	private SectorDaoService sectorDaoService;
	@Autowired
	private UnitDaoService unitDaoService;
	@Autowired
	private SectorDao sectorDao;
	@Autowired
	private Spring spring;

	private final WorldView worldView;
	private boolean unknown;
	private MoveSeen moveSeen;

	private UnitsToMove unitsToMove;
	private WorldSector targetSector;
	private Attack attack;
	private Passengers passengers;

	public UnitsMove(UnitsToMove unitsToMove, WorldView worldView) {
		this.unitsToMove = unitsToMove;
		this.worldView = worldView;
		this.targetSector = worldView.getWorldSector(unitsToMove
				.getTargetCoords());
		this.attack = new Attack(targetSector);
	}

	@PostConstruct
	public void init() {
		// TODO REF I'd prefer to make these two final
		this.unknown = checkUnknown();
		this.moveSeen = new MoveSeen(unitsToMove.getNation(), sectorDaoService,
				unitDaoService);
		this.passengers = spring
				.autowire(new Passengers(unitsToMove, worldView));
	}

	private boolean checkUnknown() {
		SectorSeen sectorSeen = sectorDao.findSectorSeen(
				unitsToMove.getNation(), targetSector);
		return (sectorSeen == null || attack.isEnemyCity());
	}

	/**
	 * @return for move: success if at least one unit made it to the target and
	 *         there was no interception / interdiction for attack: same as
	 *         move, but also at least one unit attacked
	 */
	public Result<MoveCost> move() {
		Result<MoveCost> retval;
		if (unitsToMove.isLaunching()) {
			retval = launchRocket();
		} else {
			retval = moveAndAttack();
		}
		moveSeen.persist();
		return retval;
	}

	private Result<MoveCost> moveAndAttack() {
		Result<None> result;
		MoveType moveType = MoveType.MOVE;
		unitsToMove.clearMovedFlag();
		result = moveUnits();
		if (result.isSuccess()
				&& attack.isAttackable(unitsToMove.getAttackType())) {
			Result<MoveType> attackResult = attack();
			result.or(interdictShipThatDidntMove());
			moveType = attackResult.getValue();
			result.and(new Result<None>(attackResult));
		}
		return new Result<MoveCost>(result.getMessages(), result.isSuccess(),
				new MoveCost(moveType), result.getBattleLogs(),
				result.isMoveSuccess());
	}

	private Result<None> interdictShipThatDidntMove() {
		if (unitsToMove.getAttackType() != AttackType.INITIAL_ATTACK) {
			return Result.falseInstance();
		}
		if (unitsToMove.isEmpty()) {
			return Result.falseInstance();
		}
		Unit ship = unitsToMove.getFirstUnit();
		if (!ship.isNavy()) {
			return Result.falseInstance();
		}
		if (ship.isMoved()) {
			return Result.falseInstance();
		}
		return interdict(ship.getCoords(), unitsToMove.getTargetCoords());
	}

	private Result<MoveCost> launchRocket() {
		Result<None> result;
		MoveType moveType = MoveType.MOVE;
		Unit firstUnit = unitsToMove.getFirstUnit();
		if (firstUnit.devastates()) {
			moveType = MoveType.LAUNCH_ICBM;
		} else {
			moveType = MoveType.LAUNCH_SATELLITE;
		}
		LaunchRocket launchRocket = unitCommandFactory.getLaunchRocket(
				worldView, firstUnit, unitsToMove.getTargetCoords(), moveSeen);
		result = launchRocket.launch(true);
		return new Result<MoveCost>(result.getMessages(), result.isSuccess(),
				new MoveCost(moveType), result.getBattleLogs(),
				result.isMoveSuccess());
	}

	private Result<None> moveUnits() {
		Result<None> moveResult = unitsToMove.checkColocation();
		if (!moveResult.isSuccess()) {
			return moveResult;
		}

		filterOutOfRangeUnits(moveResult);
		if (!moveResult.isSuccess()) {
			return moveResult;
		}

		List<SectorCoords> path = getPath();
		if (path == null) {
			return new Result<None>("Unable to find a path from "
					+ unitsToMove.getFirstCoords() + " to "
					+ unitsToMove.getTargetCoords(), false);
		}
		if (path.isEmpty()) {
			// Already there
			return Result.trueInstance();
		}
		return moveUnitsAlongPath(path);
	}

	private Result<None> moveUnitsAlongPath(List<SectorCoords> path) {
		Result<None> retval = Result.falseInstance();
		for (SectorCoords coords : path) {
			Result<None> moveUnitsOneSectorResult = moveUnitsOneSector(coords);
			retval.or(moveUnitsOneSectorResult);
			if (!retval.isSuccess()) {
				retval.addMessage("Units stopped short at " + coords);
				break;
			}
			sectorDaoService.updateSeen(worldView, unitsToMove.getUnits(),
					moveSeen);
			moveSeen.persistSeen(); // Intermediate persist so enemies can see
									// me to interdict me
			movePassengers(coords);
			AttackType attackType = unitsToMove.getAttackType();
			if (attackType == AttackType.INITIAL_ATTACK) {
				Result<None> interdictResult = interdict(coords, null);
				retval.and(interdictResult);
				if (!retval.isSuccess()) {
					retval.addMessage("Interdicted.  Halting movement.");
					break;
				}
			}
			if (attackType == AttackType.INITIAL_ATTACK
					|| attackType == AttackType.INTERDICTION) {
				Result<None> interceptResult = intercept(coords);
				retval.and(interceptResult);
				if (!retval.isSuccess()) {
					if (unitsToMove.actorIsNation()) {
						retval.addMessage("Intercepted.  Halting movement.");
					} else {
						retval.addMessage("Interdicting "
								+ unitsToMove.getNation()
								+ " naval bombers halted by your fighters.");
					}
					break;
				}
			}
		}
		return retval;
	}

	private void movePassengers(SectorCoords coords) {
		for (Unit passenger : passengers) {
			passenger.setCoords(coords);
			unitDaoService.merge(passenger);
		}
	}

	private void filterOutOfRangeUnits(Result<None> moveResult) {
		Result<List<Unit>> unitsOutOfRangeResult = unitsToMove
				.getUnitsOutOfRange(worldView, targetSector, unknown);
		List<Unit> unitsOutOfRange = unitsOutOfRangeResult.getValue();
		remove(unitsOutOfRange);
		List<Unit> unitsOrdered = addMoveOrder(unitsOutOfRange);
		clearMoveOrder();
		if (unitsToMove.isEmpty()) {
			moveResult.setSuccess(false);
			if (unitsOrdered.size() == 1) {
				moveResult
						.addMessage("Unit out of range.  Setting move order on unit.");
			} else if (unitsOrdered.size() > 1) {
				moveResult
						.addMessage("All units out of range.  Setting move order on units.");
			} else {
				moveResult.addMessages(unitsOutOfRangeResult.getMessages());
			}
		} else if (unitsOutOfRange.size() > 0) {
			moveResult
					.addMessage("Some units out of range.  Setting move order on those units.");
		}
	}

	private void clearMoveOrder() {
		for (Unit unit : unitsToMove) {
			unitDaoService.clearUnitMove(unit);
		}
	}

	private List<Unit> addMoveOrder(List<Unit> unitsOutOfRange) {
		List<Unit> unitsOrdered = Lists.newArrayList();
		SectorCoords targetCoords = unitsToMove.getTargetCoords();
		for (Unit unit : unitsOutOfRange) {
			if (couldMoveTo(unit)) {
				setUnitMove(unit, targetCoords);
				unitsOrdered.add(unit);
			}
		}
		return unitsOrdered;
	}

	private boolean couldMoveTo(Unit unit) {
		Movement movement = new Movement(unit, worldView);
		Result<None> result = movement.inMaxRange(unit,
				targetSector.getCoords());
		if (result.isSuccess()) {
			result.and(movement.canEnter(unitsToMove.getAttackType(),
					targetSector, unit, unknown));
		}
		return result.isSuccess();
	}

	private void setUnitMove(Unit unit, SectorCoords targetCoords) {
		unitDaoService.setUnitMove(unit, targetCoords);
	}

	/**
	 * @param range
	 * @return success means no interdiction
	 */
	private Result<None> interdict(SectorCoords coords,
			SectorCoords excludeCoords) {
		if (!unitsToMove.shipIsMoving()) {
			return Result.trueInstance();
		}
		// Only ships are interdicted
		WorldSector worldSector = worldView.getWorldSector(coords);
		// No interdiction into cannons
		if (worldSector.getCannons() > 0) {
			return Result.trueInstance();
		}
		Unit ship = unitsToMove.getFirstUnit();
		Interdiction interdiction = unitCommandFactory.getInterdiction(ship,
				excludeCoords);
		return interdiction.interdict();
	}

	/**
	 * @return success means no interception
	 */
	private Result<None> intercept(SectorCoords coords) {
		if (unitsToMove.nothingToIntercept()) {
			return Result.trueInstance();
		}
		WorldSector worldSector = worldView.getWorldSector(coords);
		// No interception over flak
		if (worldSector.getFlak() > 0) {
			return Result.trueInstance();
		}
		// Only land and air get interdicted.
		unitsToMove.clearInterceptionFlag();
		Interception interception = unitCommandFactory.getInterception(
				unitsToMove, coords);
		return interception.intercept();
	}

	private Result<None> moveUnitsOneSector(SectorCoords coords) {

		Result<None> retval = Result.falseInstance();
		List<Unit> stoppedUnits = new ArrayList<Unit>();

		WorldSector worldSector = worldView.getWorldSector(coords);
		for (Unit unit : unitsToMove) {
			// Refresh this sector view in case the target sector is a transport
			// filling up
			worldSector = sectorDaoService.refreshWorldSector(unit.getNation(),
					worldView, worldSector);
			UnitMoves unitMoves = unitCommandFactory.getUnitMoves(unitsToMove,
					unit, worldSector, worldView);
			Result<None> result = unitMoves.moveOneSector();

			if (result.isSuccess()) {
				unit.setMoved(true);
			} else {
				stoppedUnits.add(unit);
			}
			retval.or(result);

		}
		remove(stoppedUnits);

		return retval;
	}

	private void remove(List<Unit> unitsToRemove) {
		if (unitsToRemove.isEmpty()) {
			return;
		}
		List<Unit> newUnits = Lists.newArrayList(unitsToMove);
		for (Unit unit : unitsToRemove) {
			newUnits.remove(unit);
		}
		unitsToMove = UnitsToMove.copyFrom(unitsToMove, newUnits);
		passengers = Passengers.copyFrom(passengers, unitsToMove);
	}

	private Result<MoveType> attack() {
		Result<None> result = Result.falseInstance();
		MoveType moveType = MoveType.MOVE;
		for (Unit unit : unitsToMove) {
			// Refresh this sector view in case any target units were killed
			targetSector = sectorDaoService.refreshWorldSector(
					unitsToMove.getNation(), worldView, targetSector);
			attack = new Attack(targetSector);
			if (!attack.isAttackable(unitsToMove.getAttackType())) {
				if (targetSector.isPlayerCity()) {
					result.addMessage("Target sector captured.  Not moving any more units.");
				} else {
					result.addMessage("All enemy units defeated.  Not moving any more units.");
				}
				break;
			}
			Result<None> attackResult = attackWith(unit);
			if (attackResult.isSuccess()) {
				moveType = MoveType.ATTACK;
			}
			result.or(attackResult);
		}
		if (tookCity()) {
			moveType = MoveType.TAKE_CITY;
		}
		return new Result<MoveType>(result.getMessages(), result.isSuccess(),
				moveType, result.getBattleLogs(), result.isMoveSuccess());
	}

	private boolean tookCity() {
		Nation nation = unitsToMove.getNation();
		// Refresh this sector view in case the ownership changed
		targetSector = sectorDaoService.refreshWorldSector(nation, worldView,
				targetSector);
		return nation.equals(targetSector.getNation());
	}

	private Result<None> attackWith(Unit unit) {
		UnitAttacksSector unitAttacker = unitCommandFactory
				.getUnitAttacksSector(unitsToMove.getActor(),
						unitsToMove.getAttackType(), unit, targetSector,
						worldView, moveSeen);
		return unitAttacker.attack();
	}

	private List<SectorCoords> getPath() {
		for (Unit unit : unitsToMove) {
			Movement movement = new Movement(unit, worldView);
			List<SectorCoords> path = movement.getPath(
					unitsToMove.getAttackType(), unit,
					unitsToMove.getTargetCoords());
			if (path != null) {
				return path;
			}
		}
		return null;
	}

}
