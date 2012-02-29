package com.kenstevens.stratinit.type;


public interface CoordMeasure {

	public abstract int distance(SectorCoords source, SectorCoords target);

	public abstract int size();

}