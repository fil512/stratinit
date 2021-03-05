package com.kenstevens.stratinit.world.predicate;

import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.function.Predicate;

public class UnitWithinPredicate implements Predicate<Unit> {
    private final CoordMeasure coordMeasure;
    private final SectorCoords coords;
    private final int distance;

    public UnitWithinPredicate(CoordMeasure coordMeasure, SectorCoords coords, int distance) {
        this.coordMeasure = coordMeasure;
        this.coords = coords;
        this.distance = distance;
    }

    @Override
    public boolean test(Unit unit) {
        return unit.getCoords().distanceTo(coordMeasure, coords) <= distance;
    }
}
