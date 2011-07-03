package com.kenstevens.stratinit.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GameNameFileTest {
	@Test
	public void canReadFile() {
		assertEquals("Aachen", GameNameFile.getName(100));
	}
}
