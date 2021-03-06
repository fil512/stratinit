package com.kenstevens.stratinit.client.site.action;

import com.kenstevens.stratinit.BaseStratInitClientTest;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.GameView;
import com.kenstevens.stratinit.client.model.NationView;
import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseWithWorldTest extends BaseStratInitClientTest {
	protected static final SectorCoords CITY = new SectorCoords(0, 1);

	@Autowired
	Data db;
	protected NationView nation;

	@BeforeEach
	public void init() {
		SIGame sigame = new SIGame();
		GameView game = new GameView(sigame);
		game.setGamesize(10);
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
