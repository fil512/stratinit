package com.kenstevens.stratinit.dto;

public class SIUnitLove implements StratInitDTO {
    public String unitType;
    public int love;

    public SIUnitLove() {
    }

    public SIUnitLove(String unitType, int love) {
        this.unitType = unitType;
        this.love = love;
    }
}
