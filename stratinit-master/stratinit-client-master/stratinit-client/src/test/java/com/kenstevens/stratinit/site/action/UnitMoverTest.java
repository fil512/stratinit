package com.kenstevens.stratinit.site.action;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.util.UnitHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnitMoverTest extends WithWorldTest {
	@Autowired
	UnitMover unitMover;
	
	@Test
	public void maxRangeTest() {
		SIUnit siunit = new SIUnit();
		siunit.fuel = 1;
		siunit.mobility = 1;
		siunit.coords = new SectorCoords(0, 0);
		siunit.type = UnitType.FIGHTER;

		UnitView unit = new UnitView(nation, siunit);
		assertEquals(0, UnitHelper.maxRange(unit));
	}
	
	@Test
	public void oneFuelOneMob() {
		SIUnit siunit = new SIUnit();
		siunit.fuel = 1;
		siunit.mobility = 1;
		siunit.coords = new SectorCoords(0, 0);
		siunit.type = UnitType.FIGHTER;

		UnitView unit = new UnitView(nation, siunit);
		Result<None> result = unitMover.inRange(CITY, Lists.newArrayList(unit));
		assertTrue(result.isSuccess(), result.toString());
	}
	@Test
	public void oneFuelTwoMob() {
		SIUnit siunit = new SIUnit();
		siunit.fuel = 1;
		siunit.mobility = 2;
		siunit.coords = new SectorCoords(0, 0);
		siunit.type = UnitType.FIGHTER;

		UnitView unit = new UnitView(nation, siunit);
		Result<None> result = unitMover.inRange(CITY, Lists.newArrayList(unit));
		assertTrue(result.isSuccess(), result.toString());
	}
	@Test
	public void twoFuelOneMob() {
		SIUnit siunit = new SIUnit();
		siunit.fuel = 2;
		siunit.mobility = 1;
		siunit.coords = new SectorCoords(0, 0);
		siunit.type = UnitType.FIGHTER;

		UnitView unit = new UnitView(nation, siunit);
		Result<None> result = unitMover.inRange(CITY, Lists.newArrayList(unit));
		assertTrue(result.isSuccess(), result.toString());
	}
}
