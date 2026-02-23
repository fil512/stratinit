package com.kenstevens.stratinit.remote.request;

import com.kenstevens.stratinit.dto.SIUnit;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class SIUnitListJson implements IRestRequestJson {
    @NotEmpty
    public List<SIUnit> siunits;

    public SIUnitListJson() {
    }

    public SIUnitListJson(List<SIUnit> units) {
        this.siunits = siunits;
    }

    public SIUnitListJson(SIUnit siUnit) {
        this.siunits = List.of(siUnit);
    }
}
