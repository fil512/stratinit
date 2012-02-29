package com.kenstevens.stratinit.server.remote.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.Constants;

public class TechUpdateTest extends TwoPlayerBase {
	private int secondsInDay = 60 * 60 * 24;
	private double FRACTION = (double) Constants.TECH_UPDATE_INTERVAL_SECONDS / secondsInDay;
	private double MY_TECH_GAIN = Constants.TECH_INCREASE_DAILY_BY_NUM_TECH_CENTRES[1] * FRACTION;
	private double NATION2_TECH = 8.0;
	private TechUpdateEvent techUpdateEvent;

	@Before
	public void techEvent() {
		techUpdateEvent = spring.autowire(new TechUpdateEvent( testGame ));
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
		gameDao.persist(nation2);
		techUpdateEvent.execute();
		Nation nation1 = gameDao.getNation(testGameId, nationMeId);
		double leak = FRACTION * NATION2_TECH / Constants.OTHER_TECH_BLEED;
		assertEquals(MY_TECH_GAIN + leak, nation1.getTech(), 0.00000001);
	}

}
