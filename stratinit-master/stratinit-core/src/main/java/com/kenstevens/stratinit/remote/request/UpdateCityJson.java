package com.kenstevens.stratinit.remote.request;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.remote.UpdateCityField;

public class UpdateCityJson implements IRestRequestJson {
    public SICity sicity;
    public UpdateCityField field;

    public UpdateCityJson() {
    }

    public UpdateCityJson(SICity sicity, UpdateCityField field) {
        this.sicity = sicity;
        this.field = field;
    }
}
