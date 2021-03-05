package com.kenstevens.stratinit.cache;

import com.google.common.collect.Maps;
import com.kenstevens.stratinit.client.model.CityMove;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.Collection;
import java.util.Map;

public class CityMoveCache extends Cacheable {
	private final Map<SectorCoords, CityMove> cityMoveMap = Maps.newHashMap();

	public void add(CityMove cityMove) {
		cityMoveMap.put(cityMove.getCity().getCoords(), cityMove);
	}

	public CityMove get(SectorCoords coords) {
		return cityMoveMap.get(coords);
	}

	public Collection<CityMove> getCityMoves() {
		return cityMoveMap.values();
	}

	public void remove(SectorCoords coords) {
		cityMoveMap.remove(coords);
	}
}
