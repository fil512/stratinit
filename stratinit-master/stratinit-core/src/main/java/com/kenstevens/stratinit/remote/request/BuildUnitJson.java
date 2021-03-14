package com.kenstevens.stratinit.remote.request;

import com.kenstevens.stratinit.dto.SICityUpdate;
import com.kenstevens.stratinit.type.UnitType;

public class BuildUnitJson implements IRestRequestJson {
    public SICityUpdate sicity;
    public UnitType choice;

    public BuildUnitJson() {
    }

    public BuildUnitJson(SICityUpdate sicity, UnitType choice) {
        this.sicity = sicity;
        this.choice = choice;
    }
}
