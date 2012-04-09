package com.kenstevens.stratinit.type;


public interface CoordMeasure {

	int distance(SectorCoords source, SectorCoords target);

	int size();

}