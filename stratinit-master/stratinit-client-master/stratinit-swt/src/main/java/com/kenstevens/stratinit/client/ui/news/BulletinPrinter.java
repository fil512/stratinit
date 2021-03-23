package com.kenstevens.stratinit.client.ui.news;

import com.kenstevens.stratinit.dto.news.SINewsBulletin;
import org.eclipse.swt.custom.StyledText;

import java.util.List;

public class BulletinPrinter extends NewsListPrinter<SINewsBulletin>{

	public BulletinPrinter(StyledText styledText) {
		super(styledText);
	}

	@Override
	public void print(List<SINewsBulletin> items) {
		if (items.isEmpty()) {
			return;
		}
		printTitle("Bulletins");
		for (SINewsBulletin bulletin : items) {
			append(bulletin.message);
		}
	}

}
