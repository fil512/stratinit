package com.kenstevens.stratinit.ui.news;

import com.kenstevens.stratinit.dto.news.SINewsFirst;
import org.eclipse.swt.custom.StyledText;

import java.util.List;

public class FirstsPrinter extends
		NewsListPrinter<SINewsFirst> {

	public FirstsPrinter(StyledText styledText) {
		super(styledText);
	}

	@Override
	public void print(List<SINewsFirst> items) {
		if (items.isEmpty()) {
			return;
		}
		printTitle("Firsts");

		for (SINewsFirst sinuk : items) {
			String line = sinuk.nationName+" is the first nation to build a "+translate(sinuk.unitType);
			append(line);
		}
	}

}
