package com.kenstevens.stratinit.dto;

import java.util.Map;

public class SIUnitDayRow implements StratInitDTO {
    public int day;
    public Map<String, Integer> counts;

    public SIUnitDayRow() {
    }

    public SIUnitDayRow(int day, Map<String, Integer> counts) {
        this.day = day;
        this.counts = counts;
    }
}
