package com.kenstevens.stratinit.client.server.event;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.server.rest.StratInitDaoBase;
import com.kenstevens.stratinit.server.event.CityBuildEvent;
import com.kenstevens.stratinit.server.event.EventFactory;
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