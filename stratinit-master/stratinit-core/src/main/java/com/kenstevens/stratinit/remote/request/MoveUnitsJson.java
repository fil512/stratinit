package com.kenstevens.stratinit.remote.request;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.List;

public class MoveUnitsJson implements IRestRequestJson {
    public List<SIUnit> units;
    public SectorCoords target;

    public MoveUnitsJson() {
    }

    public MoveUnitsJson(List<SIUnit> units, SectorCoords target) {
        this.units = units;
        this.target = target;
    }
}
