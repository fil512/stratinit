package com.kenstevens.stratinit.remote.request;

import com.kenstevens.stratinit.dto.SICityUpdate;

public class CedeCityJson implements IRestRequestJson {
    public SICityUpdate city;
    public int nationId;

    public CedeCityJson() {
    }

    public CedeCityJson(SICityUpdate city, int nationId) {
        this.city = city;
        this.nationId = nationId;
    }
}
