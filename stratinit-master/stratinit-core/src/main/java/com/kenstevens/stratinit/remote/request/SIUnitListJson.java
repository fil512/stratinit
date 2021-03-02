package com.kenstevens.stratinit.remote.request;

import com.kenstevens.stratinit.dto.SIUnit;

import java.util.List;

public class SIUnitListJson implements IRestRequestJson {
    public List<SIUnit> siunits;

    public SIUnitListJson() {
    }

    public SIUnitListJson(List<SIUnit> units) {
        this.siunits = siunits;
    }
}
