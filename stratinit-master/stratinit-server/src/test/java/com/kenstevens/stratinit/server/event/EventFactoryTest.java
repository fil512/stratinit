package com.kenstevens.stratinit.server.event;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.server.rest.StratInitDaoBase;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EventFactoryTest extends StratInitDaoBase {
    @Autowired
    EventFactory eventFactory;

    @Test
    public void testFactoryWiring() {
        Game game = new Game();
        Nation nation = new Nation(game, null);
        Sector sector = new Sector(game, null, null);
        City city = new City(sector, nation, UnitType.INFANTRY);
        CityBuildEvent cityBuildEvent = eventFactory.getCityBuildEvent(city);
        assertNotNull(cityBuildEvent.getEventQueueForUnitTest());
    }

}