package com.kenstevens.stratinit.remote.request;

import com.kenstevens.stratinit.dto.SICityUpdate;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;

public class UpdateCityJson implements IRestRequestJson {
    public SICityUpdate sicity;

    public UpdateCityJson() {
    }

    public UpdateCityJson(SICityUpdate sicity) {
        this.sicity = sicity;
    }

    public UpdateCityJson(SICityUpdate sicity, CityFieldToUpdateEnum field) {
        this(sicity);
        this.sicity.field = field;
    }
}
