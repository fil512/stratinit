package com.kenstevens.stratinit.ui.news;

import com.kenstevens.stratinit.dto.news.SINewsFromTheFront;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.type.UnitType;
import org.eclipse.swt.custom.StyledText;

import java.util.List;

public class NewsFromTheFrontPrinter extends
		NewsListPrinter<SINewsFromTheFront> {

	public NewsFromTheFrontPrinter(StyledText styledText) {
		super(styledText);
	}

	@Override
	public void print(List<SINewsFromTheFront> list) {
		if (list.isEmpty()) {
			return;
		}
		printSubSubTitle("News From The Front");

		boolean printed = false;
		for (SINewsFromTheFront sicon : list) {
			if (!sicon.killed) {
				String line = sicon.nationName+" "+translate(sicon.nationUnitType)+
				" "+attackVerb(sicon.nationUnitType, sicon.opponentUnitType)+" "+
				sicon.opponentName+" "+translate(sicon.opponentUnitType)+
				(sicon.count > 1 && sicon.opponentUnitType != UnitType.INFANTRY ? "s "+sicon.count+" times" : "");
				append(line);
				printed = true;
			}
		}
		if (printed) {
			append("");
		}

		for (SINewsFromTheFront sicon : list) {
			if (sicon.killed) {
				String line = sicon.nationName+" "+translate(sicon.nationUnitType)+
				" "+destroyVerb(sicon.nationUnitType, sicon.opponentUnitType)+" "+
				sicon.count+" "+sicon.opponentName+" "+translate(sicon.opponentUnitType)+
				(sicon.count > 1 && sicon.opponentUnitType != UnitType.INFANTRY ? "s" : "");
				append(line);
			}
		}
	}

	private String destroyVerb(UnitType nationUnitType,
			UnitType opponentUnitType) {
		 UnitBase nationUnitBase = UnitBase.getUnitBase(nationUnitType);
		 UnitBase opponentUnitBase = UnitBase.getUnitBase(opponentUnitType);

		 if (opponentUnitBase.isAir()) {
			 if (nationUnitBase.isBomber()) {
				 return "demolished";
			 } else {
				 return "shot down";
			 }
		 } else if (opponentUnitBase.isNavy()) {
			 return "sunk";
		 } else if (opponentUnitBase.isLaunchable()) {
			 return "demolished";
		 } else if (opponentUnitType == UnitType.INFANTRY) {
			 return "killed";
		 }
		return "destroyed";
	}

	private String attackVerb(UnitType nationUnitType, UnitType opponentUnitType) {
		 UnitBase nationUnitBase = UnitBase.getUnitBase(nationUnitType);
		 UnitBase opponentUnitBase = UnitBase.getUnitBase(opponentUnitType);
		 if (nationUnitBase.isBomber()) {
			 return "bombed";
		 } else if (nationUnitBase.isAir() && opponentUnitBase.isLand()) {
			 return "strafed";
		 } else if (nationUnitBase.isSubmarine() && opponentUnitBase.isNavy()) {
			 return "torpedoed";
		 }
		return "attacked";
	}
}
