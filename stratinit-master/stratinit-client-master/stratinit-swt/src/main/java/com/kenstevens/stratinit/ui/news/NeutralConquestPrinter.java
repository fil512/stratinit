package com.kenstevens.stratinit.ui.news;

import com.kenstevens.stratinit.dto.news.SINewsNeutralConquest;
import org.eclipse.swt.custom.StyledText;

import java.util.List;

public class NeutralConquestPrinter extends
		NewsListPrinter<SINewsNeutralConquest> {

	public NeutralConquestPrinter(StyledText styledText) {
		super(styledText);
	}

	@Override
	public void print(List<SINewsNeutralConquest> list) {
		if (list.isEmpty()) {
			return;
		}
		printTitle("Exploration");

		for (SINewsNeutralConquest sicon : list) {
			String line = sicon.nationName+" captured "+sicon.count+" neutral cities";
			append(line);
		}
	}
}
