package com.kenstevens.stratinit.cache;

import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.model.SectorSeen;
import com.kenstevens.stratinit.client.model.World;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SectorSeenCache extends Cacheable {
	private final Map<SectorCoords, SectorSeen> sectorSeenMap = new ConcurrentHashMap<>();

	public void add(SectorSeen sectorSeen) {
		sectorSeenMap.put(sectorSeen.getCoords(), sectorSeen);
	}

	public SectorSeen get(SectorCoords coords) {
		return sectorSeenMap.get(coords);
	}

	public Collection<Sector> getSectorsSeenSectors(final World world) {
		return new ArrayList<>(sectorSeenMap.values()).stream()
				.map(sectorSeen -> world.getSectorOrNull(sectorSeen.getCoords()))
				.collect(Collectors.toList());
	}

	public Collection<SectorSeen> getSectorsSeen() {
		return new ArrayList<>(sectorSeenMap.values());
	}
}
