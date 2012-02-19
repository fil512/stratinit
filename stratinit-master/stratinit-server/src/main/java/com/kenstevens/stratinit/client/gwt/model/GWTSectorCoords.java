package com.kenstevens.stratinit.client.gwt.model;

import java.io.Serializable;

public class GWTSectorCoords implements Serializable {
	private static final long serialVersionUID = 1L;
	public int x;
	public int y;

	public GWTSectorCoords() {
	}

	public GWTSectorCoords(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public GWTSectorCoords(int size, int x, int y) {
		this(norm(size, x), norm(size, y));
	}
	
	public GWTSectorCoords(GWTSectorCoords coords) {
		this(coords.x, coords.y);
	}

	private static int norm(int size, int a) {
		return (((a) < 0) ? (size - 1 - ((-(a) - 1) % size)) : ((a) % size));
	}

	@Override
	public String toString() {
		return x+","+y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GWTSectorCoords other = (GWTSectorCoords) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public GWTSectorCoords shift(int size, GWTSectorCoords coords) {
		return new GWTSectorCoords(size, this.x + coords.x, this.y + coords.y);
	}

}
