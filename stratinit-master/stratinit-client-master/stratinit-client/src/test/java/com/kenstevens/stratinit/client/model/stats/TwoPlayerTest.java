package com.kenstevens.stratinit.client.model.stats;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.type.SectorCoords;
import org.junit.jupiter.api.BeforeEach;

public abstract class TwoPlayerTest {
	protected SectorCoords coords = new SectorCoords(0, 0);
	protected Player playerA = new Player("A");
	protected Player playerB = new Player("B");
	protected Game game = new Game();
	protected Nation nationA = new Nation(game, playerA);
	protected Nation nationB = new Nation(game, playerB);

	@BeforeEach
	public void init() {
		playerA.setId(1);
		playerA.setId(2);
		nationA.setNationId(1);
		nationB.setNationId(2);

	}
}
