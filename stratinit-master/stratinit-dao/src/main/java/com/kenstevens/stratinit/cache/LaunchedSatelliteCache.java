package com.kenstevens.stratinit.cache;

import com.kenstevens.stratinit.client.model.LaunchedSatellite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LaunchedSatelliteCache extends Cacheable {
	private final Map<Integer, LaunchedSatellite> satelliteMap = new ConcurrentHashMap<>();

	public List<LaunchedSatellite> getLaunchedSatellites() {
		return new ArrayList<>(satelliteMap.values());
	}

	public void add(LaunchedSatellite sat) {
		satelliteMap.put(sat.getSatelliteId(), sat);
	}

	public void remove(LaunchedSatellite sat) {
		satelliteMap.remove(sat.getSatelliteId());
	}
}
