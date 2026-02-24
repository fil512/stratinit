package com.kenstevens.stratinit.type;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SectorCoordsTest {

	private static final int SIZE = 40;

	@Test
	void neighboursReturns6() {
		SectorCoords center = new SectorCoords(5, 5);
		List<SectorCoords> neighbours = center.neighbours(SIZE);
		assertEquals(6, neighbours.size());
		// All neighbors should be unique
		assertEquals(6, new HashSet<>(neighbours).size());
	}

	@Test
	void neighboursAtOriginReturns6() {
		SectorCoords center = new SectorCoords(0, 0);
		List<SectorCoords> neighbours = center.neighbours(SIZE);
		assertEquals(6, neighbours.size());
	}

	@Test
	void sectorsWithinDistance1Returns7IncludingSelf() {
		SectorCoords center = new SectorCoords(5, 5);
		List<SectorCoords> sectors = center.sectorsWithin(SIZE, 1, true);
		assertEquals(7, sectors.size());
	}

	@Test
	void sectorsWithinDistance2Returns19IncludingSelf() {
		SectorCoords center = new SectorCoords(5, 5);
		List<SectorCoords> sectors = center.sectorsWithin(SIZE, 2, true);
		assertEquals(19, sectors.size());
	}

	@Test
	void sectorsWithinDistance2Returns18ExcludingSelf() {
		SectorCoords center = new SectorCoords(5, 5);
		List<SectorCoords> sectors = center.sectorsWithin(SIZE, 2, false);
		assertEquals(18, sectors.size());
	}

	@Test
	void sectorsWithinDistance5Returns91IncludingSelf() {
		SectorCoords center = new SectorCoords(10, 10);
		List<SectorCoords> sectors = center.sectorsWithin(SIZE, 5, true);
		// Hex circle: 3*d^2 + 3*d + 1 = 3*25 + 15 + 1 = 91
		assertEquals(91, sectors.size());
	}

	@Test
	void distanceToAdjacentIsOne() {
		SectorCoords a = new SectorCoords(0, 0);
		// (1,0) is a hex neighbor in even-Q flat-top
		assertEquals(1, SectorCoords.distance(SIZE, a, new SectorCoords(1, 0)));
	}

	@Test
	void distanceToSameIsZero() {
		SectorCoords a = new SectorCoords(5, 5);
		assertEquals(0, SectorCoords.distance(SIZE, a, a));
	}

	@Test
	void distanceStraightLineColumn() {
		// Moving along x axis: (0,0) to (2,0) = 2 in hex
		SectorCoords a = new SectorCoords(0, 0);
		SectorCoords b = new SectorCoords(2, 0);
		assertEquals(2, SectorCoords.distance(SIZE, a, b));
	}

	@Test
	void toroidalWrapX() {
		// (0,0) to (39,0) should be distance 1 on a 40-wide board
		SectorCoords a = new SectorCoords(0, 0);
		SectorCoords b = new SectorCoords(39, 0);
		assertEquals(1, SectorCoords.distance(SIZE, a, b));
	}

	@Test
	void toroidalWrapY() {
		// (0,0) to (0,39) should be distance 1 on a 40-tall board
		SectorCoords a = new SectorCoords(0, 0);
		SectorCoords b = new SectorCoords(0, 39);
		assertEquals(1, SectorCoords.distance(SIZE, a, b));
	}

	@Test
	void neighboursWrapsAtBoardEdge() {
		SectorCoords edge = new SectorCoords(0, 0);
		List<SectorCoords> neighbours = edge.neighbours(SIZE);
		assertEquals(6, neighbours.size());
		// All should be valid coords (0..SIZE-1)
		for (SectorCoords n : neighbours) {
			assertTrue(n.x >= 0 && n.x < SIZE, "x out of range: " + n.x);
			assertTrue(n.y >= 0 && n.y < SIZE, "y out of range: " + n.y);
		}
	}

	@Test
	void allNeighboursAreDistanceOne() {
		SectorCoords center = new SectorCoords(10, 10);
		for (SectorCoords n : center.neighbours(SIZE)) {
			assertEquals(1, SectorCoords.distance(SIZE, center, n),
					"Neighbour " + n + " should be distance 1 from " + center);
		}
	}

	@Test
	void sectorsWithinAreAllWithinDistance() {
		SectorCoords center = new SectorCoords(10, 10);
		int d = 3;
		for (SectorCoords s : center.sectorsWithin(SIZE, d, true)) {
			assertTrue(SectorCoords.distance(SIZE, center, s) <= d,
					"Sector " + s + " should be within distance " + d + " of " + center);
		}
	}

	@Test
	void noNeighbourDuplicates() {
		SectorCoords center = new SectorCoords(20, 20);
		List<SectorCoords> within = center.sectorsWithin(SIZE, 3, true);
		Set<SectorCoords> unique = new HashSet<>(within);
		assertEquals(unique.size(), within.size(), "No duplicates expected");
	}

	@Test
	void evenColumnNeighbourParity() {
		// Even column (0): neighbors should include specific hex offsets
		SectorCoords even = new SectorCoords(4, 4);
		List<SectorCoords> neighbours = even.neighbours(SIZE);
		Set<SectorCoords> nSet = new HashSet<>(neighbours);
		// For even-Q flat-top, even column neighbors:
		// (col+1, row), (col-1, row), (col, row+1), (col, row-1),
		// (col+1, row-1), (col-1, row-1)
		assertTrue(nSet.contains(new SectorCoords(5, 4)), "should contain (5,4)");
		assertTrue(nSet.contains(new SectorCoords(3, 4)), "should contain (3,4)");
		assertTrue(nSet.contains(new SectorCoords(4, 5)), "should contain (4,5)");
		assertTrue(nSet.contains(new SectorCoords(4, 3)), "should contain (4,3)");
	}

	@Test
	void oddColumnNeighbourParity() {
		// Odd column (5): neighbors should include specific hex offsets
		SectorCoords odd = new SectorCoords(5, 5);
		List<SectorCoords> neighbours = odd.neighbours(SIZE);
		Set<SectorCoords> nSet = new HashSet<>(neighbours);
		// For even-Q flat-top, odd column neighbors:
		// (col+1, row), (col-1, row), (col, row+1), (col, row-1),
		// (col+1, row+1), (col-1, row+1)
		assertTrue(nSet.contains(new SectorCoords(6, 5)), "should contain (6,5)");
		assertTrue(nSet.contains(new SectorCoords(4, 5)), "should contain (4,5)");
		assertTrue(nSet.contains(new SectorCoords(5, 6)), "should contain (5,6)");
		assertTrue(nSet.contains(new SectorCoords(5, 4)), "should contain (5,4)");
	}
}
