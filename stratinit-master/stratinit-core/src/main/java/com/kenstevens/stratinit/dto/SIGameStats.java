package com.kenstevens.stratinit.dto;

import java.util.Date;
import java.util.List;

public class SIGameStats implements StratInitDTO {
    public int gameId;
    public String gamename;
    public int gamesize;
    public Date startTime;
    public Date ends;
    public int duration;
    public boolean blitz;
    public List<SINationStats> nations;

    public static class SINationStats {
        public String name;
        public int cities;
        public int power;

        public SINationStats() {
        }

        public SINationStats(String name, int cities, int power) {
            this.name = name;
            this.cities = cities;
            this.power = power;
        }
    }
}
