package com.kenstevens.stratinit.server.rest.commands;

import com.kenstevens.stratinit.util.GameNameFile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameNameFileTest {
	@Test
	public void canReadFile() {
		assertEquals("Aachen", GameNameFile.getName(100));
	}
}
