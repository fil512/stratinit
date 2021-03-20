package com.kenstevens.stratinit.client.ui.news;

import com.kenstevens.stratinit.client.model.UnitBase;
import com.kenstevens.stratinit.dto.news.SINewsAirDefense;
import org.eclipse.swt.custom.StyledText;

import java.util.List;

public class AirDefensePrinter extends
		NewsListPrinter<SINewsAirDefense> {

	public AirDefensePrinter(StyledText styledText) {
		super(styledText);
	}

	@Override
	public void print(List<SINewsAirDefense> list) {
		if (list.isEmpty()) {
			return;
		}
		printSubSubTitle("Static Defenses");

		for (SINewsAirDefense sicon : list) {
			UnitBase unitBase = UnitBase.getUnitBase(sicon.nationUnitType);
			String line;
			if (unitBase.isAir()) {
				line = sicon.opponentName+" flak shot down "+sicon.count+" "+sicon.nationName+" "+translate(sicon.nationUnitType)+
			(sicon.count > 1 ? "s" : "");
			} else {
				line = sicon.opponentName+" cannons sunk "+sicon.count+" "+sicon.nationName+" "+translate(sicon.nationUnitType)+
				(sicon.count > 1 ? "s" : "");
			}
			append(line);
		}
	}
}
