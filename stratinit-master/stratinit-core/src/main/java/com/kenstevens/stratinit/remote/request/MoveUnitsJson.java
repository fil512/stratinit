package com.kenstevens.stratinit.remote.request;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.type.SectorCoords;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class MoveUnitsJson implements IRestRequestJson {
    @NotEmpty
    public List<SIUnit> units;
    @NotNull
    public SectorCoords target;

    public MoveUnitsJson() {
    }

    public MoveUnitsJson(List<SIUnit> units, SectorCoords target) {
        this.units = units;
        this.target = target;
    }
}
