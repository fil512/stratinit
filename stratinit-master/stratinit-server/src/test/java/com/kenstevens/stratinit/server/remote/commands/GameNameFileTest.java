package com.kenstevens.stratinit.server.remote.commands;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.kenstevens.stratinit.util.GameNameFile;

public class GameNameFileTest {
	@Test
	public void canReadFile() {
		assertEquals("Aachen", GameNameFile.getName(100));
	}
}
