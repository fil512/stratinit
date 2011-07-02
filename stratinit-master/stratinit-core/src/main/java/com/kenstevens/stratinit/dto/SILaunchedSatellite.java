package com.kenstevens.stratinit.dto;

import java.io.Serializable;

import com.kenstevens.stratinit.model.LaunchedSatellite;
import com.kenstevens.stratinit.type.SectorCoords;


public class SILaunchedSatellite implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id;
	public int nationId;
	public SectorCoords coords;

	public SILaunchedSatellite() {}
	
	public SILaunchedSatellite(LaunchedSatellite unit) {
		id = unit.getSatelliteId();
		coords = unit.getCoords();
		nationId = unit.getNation().getNationId();
	}
}
