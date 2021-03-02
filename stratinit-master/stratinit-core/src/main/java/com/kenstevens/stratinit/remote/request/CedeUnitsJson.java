package com.kenstevens.stratinit.remote.request;

import com.kenstevens.stratinit.dto.SIUnit;

import java.util.List;

public class CedeUnitsJson implements IRestRequestJson {
    public List<SIUnit> siunits;
    public int nationId;

    public CedeUnitsJson() {
    }

    public CedeUnitsJson(List<SIUnit> siunits, int nationId) {
        this.siunits = siunits;
        this.nationId = nationId;
    }
}
