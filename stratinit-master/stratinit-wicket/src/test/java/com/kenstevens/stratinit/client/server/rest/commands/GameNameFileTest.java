package com.kenstevens.stratinit.client.server.rest.commands;

import com.kenstevens.stratinit.client.util.GameNameFile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameNameFileTest {
	@Test
	public void canReadFile() {
		assertEquals("Aachen", GameNameFile.getName(100));
	}
}
