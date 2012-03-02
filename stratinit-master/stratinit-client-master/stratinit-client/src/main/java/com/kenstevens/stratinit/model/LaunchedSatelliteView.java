package com.kenstevens.stratinit.model;

import com.kenstevens.stratinit.dto.SILaunchedSatellite;

public class LaunchedSatelliteView extends LaunchedSatellite {

	private static final long serialVersionUID = 1L;

	public LaunchedSatelliteView(SILaunchedSatellite input) {
		this.setSatelliteId(input.id);
		this.setCoords(input.coords);
	}

}
