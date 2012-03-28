package com.kenstevens.stratinit.cache;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;
import com.kenstevens.stratinit.model.CityMove;
import com.kenstevens.stratinit.type.SectorCoords;

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
