package com.kenstevens.stratinit.ui.news;

import java.util.List;

import org.eclipse.swt.custom.StyledText;

import com.kenstevens.stratinit.dto.news.SINewsBulletin;

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
