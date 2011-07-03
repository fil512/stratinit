package com.kenstevens.stratinit.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;


@Embeddable
public class SectorCoords implements Serializable {
	private static final long serialVersionUID = 1593824949580976747L;
	// TODO REF make final
	public int x;
	public int y;

	public SectorCoords() {
	}

	public SectorCoords(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public SectorCoords(SectorCoords coords) {
		this(coords.x, coords.y);
	}

	public SectorCoords(int size, int x, int y) {
		this(norm(size, x), norm(size, y));
	}

	public SectorCoords(int boardSize, SectorCoords coords) {
		this(boardSize, coords.x, coords.y);
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
		SectorCoords other = (SectorCoords) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "" + x + "," + y;
	}

	public SectorCoords copy() {
		return new SectorCoords(x, y);
	}

	public int distanceTo(CoordMeasure coordMeasure, SectorCoords target) {
		return distance(coordMeasure.size(), this, target);
	}

	public int euclidianDistanceSquared(CoordMeasure coordMeasure,
			SectorCoords target) {
		int xdist = distance(coordMeasure.size(), x, target.x);
		int ydist = distance(coordMeasure.size(), y, target.y);
		return xdist * xdist + ydist * ydist;
	}

	public List<SectorCoords> getNeighbours(int size) {
		return getSectorsWithin(size, 1, false);
	}

	public List<SectorCoords> getSectorsWithin(int size, int distance,
			boolean includeSelf) {
		List<SectorCoords> neighbours = new ArrayList<SectorCoords>();
		for (int sx = x - distance; sx <= x + distance; ++sx) {
			for (int sy = y - distance; sy <= y + distance; ++sy) {
				if (!includeSelf && sx == x && sy == y) {
					continue;
				}
				neighbours.add(new SectorCoords(norm(size, sx), norm(size, sy)));
			}
		}
		return neighbours;
	}

	private static int norm(int size, int a) {
		return (((a) < 0) ? (size - 1 - ((-(a) - 1) % size)) : ((a) % size));
	}

	public List<SectorCoords> getNeighbours() {
		return getNeighbours(1);
	}

	public boolean adjacentTo(CoordMeasure coordMeasure, SectorCoords coords) {
		return distanceTo(coordMeasure, coords) <= 1;
	}

	public List<SectorCoords> getNeighbours(int size, int distance) {
		return getSectorsWithin(size, distance, false);
	}

	public static int distance(int size, SectorCoords source,
			SectorCoords target) {
		int xdist = distance(size, source.x, target.x);
		int ydist = distance(size, source.y, target.y);
		return Math.max(xdist, ydist);
	}

	private static int distance(int size, int a, int b) {
		int dist = Math.abs(a - b);
		if (dist > size / 2) {
			dist = size - dist;
		}
		return dist;
	}

	public SectorCoords shift(int size, SectorCoords coords) {
		return new SectorCoords(size, this.x + coords.x, this.y + coords.y);
	}
}
