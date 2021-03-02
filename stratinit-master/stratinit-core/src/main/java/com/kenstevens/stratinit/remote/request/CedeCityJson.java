package com.kenstevens.stratinit.remote.request;

import com.kenstevens.stratinit.dto.SICity;

public class CedeCityJson implements IRestRequestJson {
    public SICity city;
    public int nationId;

    public CedeCityJson() {
    }

    public CedeCityJson(SICity city, int nationId) {
        this.city = city;
        this.nationId = nationId;
    }
}
