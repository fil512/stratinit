package com.kenstevens.stratinit.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kenstevens.stratinit.model.LaunchedSatellite;

public class LaunchedSatelliteCache extends Cacheable {
	private final List<LaunchedSatellite> satellites = new ArrayList<LaunchedSatellite>();

	public List<LaunchedSatellite> getLaunchedSatellites() {
		return Collections.unmodifiableList(satellites);
	}

	public void add(LaunchedSatellite sat) {
		satellites.add(sat);
	}

	public void remove(LaunchedSatellite sat) {
		satellites.remove(sat);
	}
}
