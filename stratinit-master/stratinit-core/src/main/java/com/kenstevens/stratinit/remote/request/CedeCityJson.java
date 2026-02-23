package com.kenstevens.stratinit.remote.request;

import com.kenstevens.stratinit.dto.SICityUpdate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CedeCityJson implements IRestRequestJson {
    @NotNull
    public SICityUpdate city;
    @Positive
    public int nationId;

    public CedeCityJson() {
    }

    public CedeCityJson(SICityUpdate city, int nationId) {
        this.city = city;
        this.nationId = nationId;
    }
}
