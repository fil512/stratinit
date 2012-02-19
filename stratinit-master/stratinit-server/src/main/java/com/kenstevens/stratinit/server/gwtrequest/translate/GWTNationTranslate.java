package com.kenstevens.stratinit.server.gwtrequest.translate;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.client.gwt.model.GWTNation;
import com.kenstevens.stratinit.client.gwt.model.GWTSectorCoords;
import com.kenstevens.stratinit.dto.SINation;

public final class GWTNationTranslate {
	private GWTNationTranslate() {}

	public static List<GWTNation> translate(List<SINation> sinations) {
		ArrayList<GWTNation> retval = Lists.newArrayList();
		for (SINation sination : sinations) {
			GWTNation gwtnation = new GWTNation(sination.nationId, sination.name);
			gwtnation.cities = sination.cities;
			gwtnation.commandPoints = sination.commandPoints;
			gwtnation.played = sination.played;
			gwtnation.power = sination.power;
			if (sination.startCoords != null) {
				gwtnation.startCoords = new GWTSectorCoords(sination.startCoords.x, sination.startCoords.y);
			}
			gwtnation.tech = sination.tech;
			gwtnation.wins = sination.wins;
			retval.add(gwtnation);
		}
		return retval;
	}
}
