package com.kenstevens.stratinit.server.rest.event;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.server.event.EventFactory;
import com.kenstevens.stratinit.server.event.TechUpdateEvent;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TechUpdateTest extends TwoPlayerBase {
    private final int secondsInDay = 60 * 60 * 24;
    private final double FRACTION = (double) Constants.TECH_UPDATE_INTERVAL_SECONDS / secondsInDay;
    private final double MY_TECH_GAIN = Constants.TECH_INCREASE_DAILY_BY_NUM_TECH_CENTRES[1] * FRACTION;
    private final double NATION2_TECH = 8.0;
    @Autowired
    private EventFactory eventFactory;
    private TechUpdateEvent techUpdateEvent;

    @BeforeEach
    public void techEvent() {
        techUpdateEvent = eventFactory.getTechUpdateEvent(testGame);
    }

    @Test
    public void techUpdate() {
        techUpdateEvent.execute();
        Nation nation1 = gameDao.getNation(testGameId, nationMeId);
        assertTrue(nation1.getTech() != 0.0);
        assertEquals(MY_TECH_GAIN, nation1.getTech(), 0.00000001);
    }

    @Test
    public void techWithLeak() {
        Nation nation2 = gameDao.getNation(testGameId, nationThemId);
        nation2.setTech(NATION2_TECH);
        gameDao.save(nation2);
        techUpdateEvent.execute();
        Nation nation1 = gameDao.getNation(testGameId, nationMeId);
        double leak = FRACTION * NATION2_TECH / Constants.OTHER_TECH_BLEED;
        assertEquals(MY_TECH_GAIN + leak, nation1.getTech(), 0.00000001);
    }

}
