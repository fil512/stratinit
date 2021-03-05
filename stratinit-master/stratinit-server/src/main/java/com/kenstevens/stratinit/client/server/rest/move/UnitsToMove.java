package com.kenstevens.stratinit.client.server.rest.move;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.client.model.AttackType;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.client.util.AttackHelper;
import com.kenstevens.stratinit.move.Movement;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.supply.Supply;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.Iterator;
import java.util.List;

public class UnitsToMove implements Iterable<Unit> {

    private final Nation nation;
    private final AttackType attackType;
    private final Nation actor;
    private final ImmutableList<Unit> units;
    private final SectorCoords targetCoords;

    public UnitsToMove(Nation actor, AttackType attackType, Nation nation,
                       List<Unit> units, SectorCoords targetCoords) {
        this.actor = actor;
        this.nation = nation;
        this.attackType = attackType;
        this.units = ImmutableList.copyOf(units);
        this.targetCoords = targetCoords;
    }

    public static UnitsToMove copyFrom(UnitsToMove unitsToMove,
                                       List<Unit> newUnits) {
        return new UnitsToMove(unitsToMove.getActor(),
                unitsToMove.getAttackType(), unitsToMove.getNation(), newUnits,
                unitsToMove.getTargetCoords());
    }

    public Nation getNation() {
        return nation;
    }

    public AttackType getAttackType() {
        return attackType;
    }

    public Nation getActor() {
        return actor;
    }

    public SectorCoords getTargetCoords() {
        return targetCoords;
    }

    public Unit getFirstUnit() {
        return units.get(0);
    }

    public SectorCoords getFirstCoords() {
        if (units.isEmpty()) {
            return null;
        }
        return units.get(0).getCoords();
    }

    public ImmutableList<Unit> getUnits() {
        return units;
    }

    public boolean actorIsNation() {
        return actor.equals(nation);
    }

    public boolean shipIsMoving() {
        if (units.size() != 1) {
            return false;
        }
        return units.get(0).isNavy();
    }

    public boolean isLaunching() {
        if (units.size() != 1) {
            return false;
        }
        return units.get(0).isLaunchable();
    }

    public boolean nothingToIntercept() {
        return shipIsMoving() || isLaunching() || units.isEmpty();
    }

    public void clearInterceptionFlag() {
        for (Unit unit : units) {
            unit.setIntercepted(false);
        }
    }

    public void clearMovedFlag() {
        for (Unit unit : units) {
            unit.setMoved(false);
        }
    }

    public Result<None> checkColocation() {
        if (units.isEmpty()) {
            return new Result<None>("Nothing to move", false);
        }
        Unit firstUnit = units.get(0);
        SectorCoords firstCoords = firstUnit.getCoords();
        int shipCount = 0;
        int launchCount = 0;
        for (Unit unit : units) {
            if (!unit.getCoords().equals(firstCoords)) {
                return new Result<None>(
                        "When moving multiple units, all units must start from the same sector.  "
                                + firstUnit + " and " + unit
                                + " are not in the same location.", false);
            }
            if (unit.isNavy()) {
                ++shipCount;
                if (shipCount > 1) {
                    return new Result<None>(
                            "Due to stacking limits, ships must be moved one at a time.",
                            false);
                }
            }
            if (unit.isLaunchable()) {
                ++launchCount;
                if (launchCount > 1) {
                    return new Result<None>(
                            "Only one satellite or icbm may be launched at a time.",
                            false);
                }
            }
        }
        return Result.trueInstance();
    }

    public int getNumberOfUnits() {
        return units.size();
    }

    @Override
    public Iterator<Unit> iterator() {
        return units.iterator();
    }

    public Result<List<Unit>> getUnitsOutOfRange(WorldView worldView,
                                                 WorldSector targetSector, boolean unknown) {

        Result<None> allFailures = Result.trueInstance();
        List<Unit> unitsOutOfRange = Lists.newArrayList();
        Supply supply = new Supply(worldView);
        for (Unit unit : units) {
            Movement movement = new Movement(unit, worldView);
            Result<None> lastResult = movement.inRange(unit,
                    targetSector.getCoords(), AttackHelper.costToMove(unit, supply.inSupply(unit)));
            if (lastResult.isSuccess()) {
                lastResult.and(movement.canEnter(attackType, targetSector,
                        unit, unknown));
            }
            if (!lastResult.isSuccess()) {
                unitsOutOfRange.add(unit);
                allFailures.or(lastResult);
            }
        }
        return new Result<List<Unit>>(allFailures.getMessages(), allFailures.isSuccess(), unitsOutOfRange);
    }

    public boolean isEmpty() {
        return units.isEmpty();
    }

}
