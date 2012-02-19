package com.kenstevens.stratinit.server.gwtrequest.translate;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.client.gwt.model.GWTCityType;
import com.kenstevens.stratinit.client.gwt.model.GWTSector;
import com.kenstevens.stratinit.client.gwt.model.GWTSectorColour;
import com.kenstevens.stratinit.client.gwt.model.GWTSectorType;
import com.kenstevens.stratinit.client.gwt.model.GWTUnitType;
import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.type.RelationType;

public final class GWTSectorTranslate {
	private GWTSectorTranslate() {}

	public static List<GWTSector> translate(List<SISector> sisectors) {
		ArrayList<GWTSector> retval = Lists.newArrayList();
		for (SISector sisector : sisectors) {
			GWTSectorColour colour = getColourFromRelation(sisector);
			GWTSectorType sectorType = GWTSectorType.valueOf(sisector.type.toString());
			GWTSector gwtsector = new GWTSector(sisector.coords.x, sisector.coords.y, sectorType, colour);
			if (sisector.cityType != null) {
				gwtsector.cityType = GWTCityType.valueOf(sisector.cityType.toString());
			}
			if (sisector.topUnitType != null) {
				gwtsector.topUnitType = GWTUnitType.valueOf(sisector.topUnitType.toString());
			}
			gwtsector.topUnitId = sisector.topUnitId;
			retval.add(gwtsector);
		}
		return retval;
	}

	private static GWTSectorColour getColourFromRelation(SISector sisector) {
		RelationType relation = sisector.myRelation;
		if (relation == RelationType.ALLIED) {
			return GWTSectorColour.gold;
		} else if (relation == RelationType.FRIENDLY) {
			return GWTSectorColour.yellow;
		} else if (relation == RelationType.ME) {
			return GWTSectorColour.aqua;
		} else if (relation == RelationType.WAR) {
			return GWTSectorColour.red;
		} else {
			return GWTSectorColour.trans;
		}
	}
}
