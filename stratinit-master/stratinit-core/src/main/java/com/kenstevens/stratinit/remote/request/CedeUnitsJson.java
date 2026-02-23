package com.kenstevens.stratinit.remote.request;

import com.kenstevens.stratinit.dto.SIUnit;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class CedeUnitsJson implements IRestRequestJson {
    @NotEmpty
    public List<SIUnit> siunits;
    @Positive
    public int nationId;

    public CedeUnitsJson() {
    }

    public CedeUnitsJson(List<SIUnit> siunits, int nationId) {
        this.siunits = siunits;
        this.nationId = nationId;
    }
}
