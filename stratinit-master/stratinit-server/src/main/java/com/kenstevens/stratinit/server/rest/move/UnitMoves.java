package com.kenstevens.stratinit.server.rest.move;

import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.Movement;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.supply.Supply;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.util.AttackHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class UnitMoves {
    private final Unit unit;
    private final WorldSector targetSector;
    private final SectorCoords targetCoords;
    private final WorldView worldView;
    private final UnitsToMove unitsToMove;
    @Autowired
    private UnitDaoService unitDaoService;
    @Autowired
    private SectorDaoService sectorDaoService;

    public UnitMoves(UnitsToMove unitsToMove, Unit unit, WorldSector targetSector, WorldView worldView) {
        this.unitsToMove = unitsToMove;
        this.unit = unit;
        this.worldView = worldView;
        this.targetSector = targetSector;
        this.targetCoords = targetSector.getCoords();
    }

    public Result<None> moveOneSector() {
        Result<None> result = new Movement(unit, worldView).canEnter(unitsToMove.getAttackType(), targetSector, unit, false);
        if (!result.isSuccess()) {
            return result;
        }
        if (landUnitMovingOntoFullTransport()) {
            return new Result<None>("Transport is full", false);
        }
        Supply supply = new Supply(worldView);
        boolean inSupply = supply.inSupply(unit);
        int cost = AttackHelper.costToMove(unit, inSupply);
        if (unit.getMobility() < cost) {
            return new Result<None>(
                    "Insufficient mobility (need " + cost + " have " + unit.getMobility() + ")", false);
        }

        if (unit.requiresFuel() && !inSupply) {
            if (unit.getFuel() == 0) {
                return new Result<None>("Insufficient fuel.", false);
            }
            unit.decreaseFuel();
        }

        unit.decreaseMobility(cost);
        unit.setCoords(targetCoords);
        unitDaoService.merge(unit);
        postMoveActions(unit);
        return Result.trueInstance();
    }

    private boolean landUnitMovingOntoFullTransport() {
        if (unit.isLand() && targetSector.isHoldsMyTransport()) {
            int landUnitWeight = sectorDaoService.getLandUnitWeight(targetSector);
            return unit.getWeight() + landUnitWeight > UnitBase.getUnitBase(
                    UnitType.TRANSPORT).getCapacity();
        }
        return false;
    }

    private void postMoveActions(Unit unit) {
        // If we moved into supply, resupply
        Supply supply = new Supply(worldView);
        if (supply.inSupply(unit)) {
            supply.resupply(unit);
        }
        // If I am a supply ship, provide supply to units around me
        if (unit.isSuppliesLand() || unit.isSuppliesNavy()) {
            sectorDaoService.explodeSupply(unit.getNation(), unit.getCoords());
        }
        // If I am a carrier, fuel ships over me
        if (unit.getType() == UnitType.CARRIER) {
            unitDaoService.resupplyAir(unit);
        }

    }


}
