package com.kenstevens.stratinit.helper;

import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.type.UnitType;

public class UnitHelper {
    public static final Unit meUnit = new Unit(NationHelper.nationMe, UnitType.INFANTRY, SectorHelper.coords);
    public static final Unit themUnit = new Unit(NationHelper.nationThem, UnitType.INFANTRY, SectorHelper.coords);

}
