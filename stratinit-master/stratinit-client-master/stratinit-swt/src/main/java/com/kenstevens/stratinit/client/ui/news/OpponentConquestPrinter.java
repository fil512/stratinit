package com.kenstevens.stratinit.client.ui.news;

import com.kenstevens.stratinit.dto.news.SINewsOpponentConquest;
import org.eclipse.swt.custom.StyledText;

import java.util.List;

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
