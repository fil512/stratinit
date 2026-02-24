package com.kenstevens.stratinit.type;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Embeddable
public class SectorCoords implements Serializable, Comparable<SectorCoords> {
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
		return y == other.y;
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
		int hexDist = distance(coordMeasure.size(), this, target);
		return hexDist * hexDist;
	}

	public List<SectorCoords> neighbours(int size) {
		return sectorsWithin(size, 1, false);
	}

	public List<SectorCoords> sectorsWithin(int size, int distance,
											boolean includeSelf) {
		List<SectorCoords> result = new ArrayList<>();
		int[] center = offsetToCube(x, y);
		int cx = center[0], cy = center[1], cz = center[2];
		for (int dx = -distance; dx <= distance; ++dx) {
			int dyMin = Math.max(-distance, -dx - distance);
			int dyMax = Math.min(distance, -dx + distance);
			for (int dy = dyMin; dy <= dyMax; ++dy) {
				int dz = -dx - dy;
				if (!includeSelf && dx == 0 && dy == 0) {
					continue;
				}
				int[] offset = cubeToOffset(cx + dx, cz + dz);
				result.add(new SectorCoords(norm(size, offset[0]), norm(size, offset[1])));
			}
		}
		return result;
	}

	static int norm(int size, int a) {
		return (((a) < 0) ? (size - 1 - ((-(a) - 1) % size)) : ((a) % size));
	}

	public List<SectorCoords> neighbours() {
		return neighbours(1);
	}

	public boolean adjacentTo(CoordMeasure coordMeasure, SectorCoords coords) {
		return distanceTo(coordMeasure, coords) <= 1;
	}

	public List<SectorCoords> neighbours(int size, int distance) {
		return sectorsWithin(size, distance, false);
	}

	public static int distance(int size, SectorCoords source,
							   SectorCoords target) {
		// For toroidal wrapping, check all 9 wrapped copies and take minimum
		int minDist = Integer.MAX_VALUE;
		for (int dx = -1; dx <= 1; ++dx) {
			for (int dy = -1; dy <= 1; ++dy) {
				int wrappedX = target.x + dx * size;
				int wrappedY = target.y + dy * size;
				int dist = hexDistance(source.x, source.y, wrappedX, wrappedY);
				if (dist < minDist) {
					minDist = dist;
				}
			}
		}
		return minDist;
	}

	private static int hexDistance(int x1, int y1, int x2, int y2) {
		int[] a = offsetToCube(x1, y1);
		int[] b = offsetToCube(x2, y2);
		return cubeDistance(a, b);
	}

	// Even-Q offset -> cube coordinates (flat-top hexagons)
	private static int[] offsetToCube(int col, int row) {
		int cx = col;
		int cz = row - (col + (col & 1)) / 2;
		int cy = -cx - cz;
		return new int[]{cx, cy, cz};
	}

	// Cube -> even-Q offset coordinates
	private static int[] cubeToOffset(int cx, int cz) {
		int col = cx;
		int row = cz + (cx + (cx & 1)) / 2;
		return new int[]{col, row};
	}

	private static int cubeDistance(int[] a, int[] b) {
		return (Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]) + Math.abs(a[2] - b[2])) / 2;
	}

	public SectorCoords shift(int size, SectorCoords coords) {
		return new SectorCoords(size, this.x + coords.x, this.y + coords.y);
	}

	@Override
	public int compareTo(SectorCoords other) {
		if (y > other.y) {
			return -1;
		} else if (y < other.y) {
			return 1;
		} else {
			return Integer.valueOf(x).compareTo(other.x);
		}
	}
}
