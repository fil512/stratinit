package com.kenstevens.stratinit.client.server.rest.move;

import com.kenstevens.stratinit.client.model.AttackType;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.client.server.daoservice.MoveSeen;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class UnitMoveFactory {
    @Lookup
    public UnitsInterceptor getUnitsInterceptor(Nation nation, WorldView worldView, Unit targetUnit, SectorCoords excludeCoords, Collection<Unit> units) {
        return new UnitsInterceptor(nation, worldView, targetUnit, excludeCoords, units);
    }

    @Lookup
    public UnitsMove getUnitsMove(UnitsToMove unitsToMove, WorldView worldView) {
        return new UnitsMove(unitsToMove, worldView);
    }

    @Lookup
    public UnitAttacksSector getUnitAttacksSector(Nation actor, AttackType attackType, Unit unit, WorldSector targetSector, WorldView worldView, MoveSeen moveSeen) {
        return new UnitAttacksSector(actor, attackType, unit, targetSector, worldView, moveSeen);
    }

    @Lookup
    public UnitAttacksUnit getUnitAttacksUnit(WorldView worldView, Nation actor, AttackType attackType, Unit unit, Unit enemyUnit, WorldSector targetSector, MoveSeen moveSeen) {
        return new UnitAttacksUnit(worldView, actor, attackType, unit, enemyUnit, targetSector, moveSeen);
    }

    @Lookup
    public UnitMoves getUnitMoves(UnitsToMove unitsToMove, Unit unit, WorldSector worldSector, WorldView worldView) {
        return new UnitMoves(unitsToMove, unit, worldSector, worldView);
    }

    @Lookup
    public UnitBombsSector getUnitBombsSector(Nation actor, Unit bomber, WorldSector targetSector, Collection<Unit> units, AttackReadiness attackReadiness) {
        return new UnitBombsSector(actor, bomber, targetSector, units, attackReadiness);
    }

    @Lookup
    public Interdiction getInterdiction(Unit targetUnit, SectorCoords excludeCoords) {
        return new Interdiction(targetUnit, excludeCoords);
    }

    @Lookup
    public Interception getInterception(UnitsToMove unitsToMove, SectorCoords coords) {
        return new Interception(unitsToMove, coords);
    }

    @Lookup
    public LaunchRocket getLaunchRocket(CoordMeasure coordMeasure, Unit unit, SectorCoords target, MoveSeen moveSeen) {
        return new LaunchRocket(coordMeasure, unit, target, moveSeen);
    }

    @Lookup
    public UnitsCede getUnitsCede(Nation nation, List<Unit> units, int nationId, WorldView worldView) {
        return new UnitsCede(nation, units, nationId, worldView);
    }

    @Lookup
    public Passengers getPassengers(UnitsToMove unitsToMove, WorldView worldView) {
        return new Passengers(unitsToMove, worldView);
    }
}
