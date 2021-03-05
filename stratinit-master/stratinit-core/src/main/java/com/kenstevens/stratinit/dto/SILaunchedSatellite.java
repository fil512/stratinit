package com.kenstevens.stratinit.dto;

import com.kenstevens.stratinit.client.model.LaunchedSatellite;
import com.kenstevens.stratinit.type.SectorCoords;


public class SILaunchedSatellite implements StratInitDTO {
    private static final long serialVersionUID = 1L;
    public int id;
    public int nationId;
    public SectorCoords coords;

    public SILaunchedSatellite() {
    }

    public SILaunchedSatellite(LaunchedSatellite unit) {
        id = unit.getSatelliteId();
        coords = unit.getCoords();
		nationId = unit.getNation().getNationId();
	}
}
