package com.kenstevens.stratinit.remote.request;

import com.kenstevens.stratinit.model.Unit;

import java.util.List;

public class UnitListJson implements IRestRequestJson {
    public List<Unit> units;

    public UnitListJson() {
    }

    public UnitListJson(List<Unit> units) {
        this.units = units;
    }
}
