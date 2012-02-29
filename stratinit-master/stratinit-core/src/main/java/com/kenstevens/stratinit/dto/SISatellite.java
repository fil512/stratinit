package com.kenstevens.stratinit.dto;

import java.io.Serializable;

import com.kenstevens.stratinit.model.LaunchedSatellite;
import com.kenstevens.stratinit.type.SectorCoords;


public class SISatellite implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id;
	public SectorCoords coords;

	public SISatellite() {}
	
	public SISatellite(LaunchedSatellite satellite) {
		id = satellite.getSatelliteId();
		coords = satellite.getCoords();
	}
}
