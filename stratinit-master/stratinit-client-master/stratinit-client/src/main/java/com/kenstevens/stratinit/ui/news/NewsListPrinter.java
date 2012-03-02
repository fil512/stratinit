package com.kenstevens.stratinit.ui.news;

import java.util.List;

import org.eclipse.swt.custom.StyledText;

import com.kenstevens.stratinit.dto.news.SINewsLog;
import com.kenstevens.stratinit.type.UnitType;

public abstract class NewsListPrinter<T extends SINewsLog> extends NewsPrinter {
	public NewsListPrinter(StyledText styledText) {
		super(styledText);
	}

	protected String translate(UnitType type) {
		if (type == UnitType.SUPPLY) {
			return "supply ship";
		}
		if (type == UnitType.PATROL) {
			return "patrol boat";
		}
		String name = type.toString().toLowerCase();
		return name.replace("_", " ");
	}

	public abstract void print(List<T> items);
}
