package com.kenstevens.stratinit.rank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class TestTeamProviderTest {
	ITeamProvider tp = new TestTeamProvider();

	@Test
	public void testGetAllies() {
		assertEquals(TestTeamProvider.nation1b, tp.getAllies(TestTeamProvider.nation1a).iterator().next());
	}

	@Test
	public void testGetNations() {
		assertEquals(5, tp.getNations(TestTeamProvider.game2).size());
	}

	@Test
	public void testGetNumberOfCities() {
		assertEquals(15, tp.getNumberOfCities(TestTeamProvider.nation1a));
		assertEquals(10, tp.getNumberOfCities(TestTeamProvider.nation1b));
		assertEquals(10, tp.getNumberOfCities(TestTeamProvider.nation2a));
		assertEquals(50, tp.getNumberOfCities(TestTeamProvider.nation2c));
		assertEquals(10, tp.getNumberOfCities(TestTeamProvider.nation2f));
	}
}
