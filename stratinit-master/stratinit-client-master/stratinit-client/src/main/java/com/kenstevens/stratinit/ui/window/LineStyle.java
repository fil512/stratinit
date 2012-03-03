package com.kenstevens.stratinit.ui.window;

import org.eclipse.swt.graphics.GC;

import com.kenstevens.stratinit.ui.image.ColourMap;

public enum LineStyle {
	UNIT_MOVE, SUPPLY_LINKED, SUPPLY_ISOLATED, CITY_MOVE;

	public void apply(GC gc) {
		switch (this) {
		case SUPPLY_LINKED:
			gc.setForeground(ColourMap.WHITE);
			gc.setAlpha(50);
			gc.setLineWidth(4);
			break;
		case SUPPLY_ISOLATED:
			gc.setForeground(ColourMap.WHITE);
			gc.setAlpha(25);
			gc.setLineWidth(2);
			break;
		case UNIT_MOVE:
			gc.setForeground(ColourMap.BLACK);
			gc.setLineWidth(2);
			break;
		case CITY_MOVE:
			gc.setForeground(ColourMap.RED);
			gc.setLineWidth(2);
			break;
		}
	}
}
