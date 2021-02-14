package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.StratInitClientTest;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.GameView;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
public class WithWorldTest extends StratInitClientTest {
	protected static final SectorCoords CITY = new SectorCoords(0, 1);

	@Autowired
	Data db;
	protected NationView nation;

	@BeforeEach
	public void init() {
		SIGame sigame = new SIGame();
		GameView game = new GameView(sigame);
		game.setSize(10);
		SINation sination = new SINation();
		nation = new NationView(game, sination);
		WorldView world = new WorldView(nation, null, null);
		WorldSector worldSector = new WorldSector(game, CITY, SectorType.PLAYER_CITY, 0);
		worldSector.setCityType(CityType.AIRPORT);
		worldSector.setMyRelation(RelationType.ME);
		worldSector.setTheirRelation(RelationType.ME);
		world.setWorldSector(worldSector);
		db.setSelectedGame(game);
		game.setWorld(world);
	}
}
