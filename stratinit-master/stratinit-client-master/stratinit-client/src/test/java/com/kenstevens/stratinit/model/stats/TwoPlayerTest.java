package com.kenstevens.stratinit.model.stats;

import org.junit.Before;
import org.junit.Ignore;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.type.SectorCoords;

@Ignore
public class TwoPlayerTest {
	protected SectorCoords coords = new SectorCoords(0,0);
	protected Player playerA = new Player("A");
	protected Player playerB = new Player("B");
	protected Game game = new Game();
	protected Nation nationA = new Nation(game, playerA);
	protected Nation nationB = new Nation(game, playerB);

	@Before
	public void init() {
		playerA.setId(1);
		playerA.setId(2);
		nationA.setNationId(1);
		nationB.setNationId(2);

	}
}
