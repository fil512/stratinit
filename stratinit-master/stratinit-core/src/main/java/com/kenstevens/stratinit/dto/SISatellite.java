package com.kenstevens.stratinit.dto;

import com.kenstevens.stratinit.model.LaunchedSatellite;
import com.kenstevens.stratinit.type.SectorCoords;


public class SISatellite implements StratInitDTO {
    private static final long serialVersionUID = 1L;
    public int id;
    public SectorCoords coords;

    public SISatellite() {
    }

    public SISatellite(LaunchedSatellite satellite) {
        id = satellite.getSatelliteId();
        coords = satellite.getCoords();
    }
}
