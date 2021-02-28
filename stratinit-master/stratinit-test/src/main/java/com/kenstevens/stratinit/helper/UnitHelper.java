package com.kenstevens.stratinit.helper;

import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.type.UnitType;

public class UnitHelper {
    public static final Unit unit = new Unit(NationHelper.nationMe, UnitType.INFANTRY, SectorHelper.coords);

}
