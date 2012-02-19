package com.kenstevens.stratinit.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.SectorSeen;
import com.kenstevens.stratinit.model.World;
import com.kenstevens.stratinit.type.SectorCoords;

public class SectorSeenCache extends Cacheable {
	private final Map<SectorCoords, SectorSeen> sectorSeenMap = new HashMap<SectorCoords, SectorSeen>();

	public void add(SectorSeen sectorSeen) {
		sectorSeenMap.put(sectorSeen.getCoords(), sectorSeen);
	}

	public SectorSeen get(SectorCoords coords) {
		return sectorSeenMap.get(coords);
	}

	public Collection<Sector> getSectorsSeenSectors(final World world) {
		return Collections2.transform(sectorSeenMap.values(), new Function<SectorSeen, Sector>() {
			@Override
			public Sector apply(SectorSeen sectorSeen) {
				return world.getSector(sectorSeen.getCoords());
			}
		});
	}

	public Collection<SectorSeen> getSectorsSeen() {
		return sectorSeenMap.values();
	}
}
