package com.kenstevens.stratinit.server.gwtrequest.translate;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.client.gwt.model.GWTUnit;
import com.kenstevens.stratinit.client.gwt.model.GWTUnitType;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public final class GWTUnitTranslate {
	private GWTUnitTranslate() {}

	public static List<GWTUnit> translate(List<SIUnit> siunits) {
		ArrayList<GWTUnit> retval = Lists.newArrayList();
		for (SIUnit siunit : siunits) {
			GWTUnitType sectorType = GWTUnitType.valueOf(siunit.type.toString());
			GWTUnit gwtunit = new GWTUnit(siunit.coords.x, siunit.coords.y, sectorType);
			gwtunit.ammo = siunit.ammo;
			gwtunit.created = siunit.created;
			gwtunit.fuel = siunit.fuel;
			gwtunit.hp = siunit.hp;
			gwtunit.id = siunit.id;
			gwtunit.lastUpdated = siunit.lastUpdated;
			gwtunit.mobility = siunit.mobility;
			gwtunit.nationId = siunit.nationId;
			retval.add(gwtunit);
		}
		return retval;
	}

	public static List<SIUnit> inTranslate(List<GWTUnit> units) {
		ArrayList<SIUnit> retval = Lists.newArrayList();
		for (GWTUnit siunit : units) {
			SIUnit gwtunit = new SIUnit();
			gwtunit.coords = new SectorCoords(siunit.coords.x, siunit.coords.y);
			gwtunit.type = UnitType.valueOf(siunit.unitType.toString());
			gwtunit.ammo = siunit.ammo;
			gwtunit.created = siunit.created;
			gwtunit.fuel = siunit.fuel;
			gwtunit.hp = siunit.hp;
			gwtunit.id = siunit.id;
			gwtunit.lastUpdated = siunit.lastUpdated;
			gwtunit.mobility = siunit.mobility;
			gwtunit.nationId = siunit.nationId;
			retval.add(gwtunit);
		}
		return retval;
	}
}
