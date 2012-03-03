package com.kenstevens.stratinit.rank;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.Iterables;
public class TestTeamProviderTest {
	TeamProvider tp = new TestTeamProvider();

	@Test
	public void testGetAllies() {
		assertEquals(TestTeamProvider.nation1b, Iterables.getOnlyElement(tp.getAllies(TestTeamProvider.nation1a)));
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
