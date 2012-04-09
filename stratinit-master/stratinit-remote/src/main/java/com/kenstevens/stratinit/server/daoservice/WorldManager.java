package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.World;

public interface WorldManager {

	World build(Game game);

	void addPlayerToMap(int island, Nation nation);

}