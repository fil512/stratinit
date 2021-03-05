package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitListTest {
	@Test
	public void unitMobilityChange() {
		SIUnit siunit = new SIUnit();
		int id = 1;
		siunit.id = id;
		siunit.type = UnitType.INFANTRY;
		siunit.mobility = 0;

		UnitList unitList = new UnitList();
		List<UnitView> units = new ArrayList<UnitView>();
		Game game = new Game();
		units.add(new UnitView(new NationView(game , new SINation()), siunit));

		unitList.addAll(units);
		assertEquals(0, unitList.get(id).getMobility());
		SIUnit siunit2 = new SIUnit();
		siunit2.id = id;
		siunit2.type = UnitType.INFANTRY;
		siunit2.mobility = 2;
		units.clear();
		units.add(new UnitView(new NationView(game, new SINation()), siunit2));
		unitList.addAll(units);
		assertEquals(2, unitList.get(id).getMobility());
	}
}
