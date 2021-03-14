package com.kenstevens.stratinit.remote.request;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;
import com.kenstevens.stratinit.type.UnitType;

public class UpdateCityJson implements IRestRequestJson {
    public SICity sicity;
    public UnitType unitType;
    public CityFieldToUpdateEnum field;

    public UpdateCityJson() {
    }

    public UpdateCityJson(SICity sicity, CityFieldToUpdateEnum field) {
        this.sicity = sicity;
        this.field = field;
    }

    public UpdateCityJson(SICity sicity, UnitType unitType, CityFieldToUpdateEnum field) {
        this.sicity = sicity;
        this.unitType = unitType;
        this.field = field;
    }
}
