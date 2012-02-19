package com.kenstevens.stratinit.server.gwtrequest.translate;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.client.gwt.model.GWTCity;
import com.kenstevens.stratinit.client.gwt.model.GWTCityType;
import com.kenstevens.stratinit.client.gwt.model.GWTUnitType;
import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.UnitType;

public final class GWTCityTranslate {
	private GWTCityTranslate() {}

	public static List<GWTCity> translate(List<SICity> sicities) {
		ArrayList<GWTCity> retval = Lists.newArrayList();
		for (SICity sicity : sicities) {
			GWTCityType cityType = GWTCityType.valueOf(sicity.type.toString());
			GWTCity gwtcity = new GWTCity(sicity.coords.x, sicity.coords.y, cityType, sicity.nationId);
			gwtcity.build= GWTUnitType.valueOf(sicity.build.toString());
			if (sicity.nextBuild != null) {
				gwtcity.nextBuild = GWTUnitType.valueOf(sicity.nextBuild.toString());
			}
			retval.add(gwtcity);
		}
		return retval;
	}

	public static SICity inTranslate(GWTCity city) {
		SICity sicity = new SICity(city.coords.x, city.coords.y, CityType.valueOf(city.cityType.toString()), city.nationId);
		sicity.build = UnitType.valueOf(city.build.toString());
		if (city.nextBuild != null) {
			sicity.nextBuild = UnitType.valueOf(city.nextBuild.toString());
		}
		return sicity;
	}
}
