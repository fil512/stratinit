package com.kenstevens.stratinit.remote.request;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.type.UnitType;

public class BuildUnitJson implements RestRequestJson {
    public SICity sicity;
    public UnitType choice;

    public BuildUnitJson() {
    }

    public BuildUnitJson(SICity sicity, UnitType choice) {
        this.sicity = sicity;
        this.choice = choice;
    }
}
