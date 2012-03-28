package com.kenstevens.stratinit.model;

import static org.junit.Assert.*;

import org.junit.Test;

import com.kenstevens.stratinit.dto.SITeam;

public class SITeamTest {

	@Test
	public void testName() {
		SITeam team = new SITeam("a", "b", 10);
		assertEquals("a b", team.getName());
		team = new SITeam("b", "a", 10);
		assertEquals("a b", team.getName());
		team = new SITeam("a", null, 10);
		assertEquals("a", team.getName());
	}

}
