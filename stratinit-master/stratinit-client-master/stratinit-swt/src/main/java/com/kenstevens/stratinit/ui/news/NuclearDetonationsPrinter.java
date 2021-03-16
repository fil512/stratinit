package com.kenstevens.stratinit.ui.news;

import com.kenstevens.stratinit.dto.news.SINewsNuclearDetonations;
import org.eclipse.swt.custom.StyledText;

import java.util.List;

public class NuclearDetonationsPrinter extends
		NewsListPrinter<SINewsNuclearDetonations> {

	public NuclearDetonationsPrinter(StyledText styledText) {
		super(styledText);
	}

	@Override
	public void print(List<SINewsNuclearDetonations> items) {
		if (items.isEmpty()) {
			return;
		}
		printSubSubTitle("Nuclear Detonations");

		for (SINewsNuclearDetonations sinuk : items) {
			String line = sinuk.nationName + " "
					+ sinuk.launchableUnit.toString().toLowerCase()
					+ "s nuked " + sinuk.count + " " + sinuk.opponentName
					+ " cities";
			append(line);
		}
	}
}
