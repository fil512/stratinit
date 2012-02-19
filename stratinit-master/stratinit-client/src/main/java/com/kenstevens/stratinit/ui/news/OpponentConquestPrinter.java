package com.kenstevens.stratinit.ui.news;

import java.util.List;

import org.eclipse.swt.custom.StyledText;

import com.kenstevens.stratinit.dto.news.SINewsOpponentConquest;

public class OpponentConquestPrinter extends
		NewsListPrinter<SINewsOpponentConquest> {

	public OpponentConquestPrinter(StyledText styledText) {
		super(styledText);
	}

	@Override
	public void print(List<SINewsOpponentConquest> list) {
		if (list.isEmpty()) {
			return;
		}
		printSubSubTitle("Conquest");

		for (SINewsOpponentConquest sicon : list) {
			String line = sicon.nationName+" captured "+sicon.count+" cities from "+sicon.opponentName;
			append(line);
		}
	}
}
