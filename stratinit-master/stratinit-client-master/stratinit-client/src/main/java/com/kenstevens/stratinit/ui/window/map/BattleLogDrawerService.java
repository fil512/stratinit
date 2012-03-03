package com.kenstevens.stratinit.ui.window.map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.model.BattleLogEntry;
import com.kenstevens.stratinit.type.NewsCategory;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.ui.image.ImageLibrary;

@Service
public class BattleLogDrawerService {
	@Autowired
	ImageLibrary imageLibrary;
	@Autowired
	MapDrawerService mapDrawer;

	public void drawDestroyedUnit(GC gc, BattleLogEntry entry) {
		if (entry.getType() != NewsCategory.NEWS_FROM_THE_FRONT) {
			return;
		}
		if (entry.iAmAttacker()) {
			if (!entry.isAttackerDied()) {
				return;
			}
		} else {
			if (!entry.isDefenderDied()) {
				return;
			}
		}
		SectorCoords coords = entry.getCoords();
		String myUnit = entry.getMyUnit();
		if (myUnit.contains(" ")) {
			myUnit = StringUtils.substringAfter(myUnit, " ");
		}
		UnitType unitType = UnitType.valueOf(myUnit.toUpperCase());
		Image image = imageLibrary.getDestroyed(unitType);
		mapDrawer.drawSquare(gc, coords, image);
	}
}
