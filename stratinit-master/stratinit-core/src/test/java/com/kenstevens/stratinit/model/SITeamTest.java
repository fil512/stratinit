package com.kenstevens.stratinit.model;

import com.kenstevens.stratinit.dto.SITeam;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
