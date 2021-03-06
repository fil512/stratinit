package com.kenstevens.stratinit.type;

public class SectorCoordVector {
	private final SectorCoords start;
	private final SectorCoords end;
	
	public SectorCoordVector(SectorCoords start, SectorCoords end) {
		this.start = start;
		this.end = end;
	}

	public SectorCoords getStart() {
		return start;
	}

	public SectorCoords getEnd() {
		return end;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
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
		SectorCoordVector other = (SectorCoordVector) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}
	
}
