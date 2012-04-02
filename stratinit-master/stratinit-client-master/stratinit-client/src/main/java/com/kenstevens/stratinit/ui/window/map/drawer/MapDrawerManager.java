package com.kenstevens.stratinit.ui.window.map.drawer;

import java.util.Set;

import org.eclipse.swt.graphics.GC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.model.BattleLogEntry;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.shell.WidgetContainer;
import com.kenstevens.stratinit.supply.Supply;
import com.kenstevens.stratinit.ui.image.ImageLibrary;
import com.kenstevens.stratinit.ui.window.map.BattleLogDrawerService;
import com.kenstevens.stratinit.ui.window.map.MapDrawerService;
import com.kenstevens.stratinit.ui.window.map.SectorDrawerService;
import com.kenstevens.stratinit.ui.window.map.SupplyDrawerService;
import com.kenstevens.stratinit.ui.window.map.UnitDrawerService;

@Service
public class MapDrawerManager {
	@Autowired
	Data db;
	@Autowired
	MapDrawerService mapDrawer;
	@Autowired
	ImageLibrary imageLibrary;
	@Autowired
	private WidgetContainer widgetContainer;
	@Autowired
	private SupplyDrawerService worldSupplyDisplayerService;
	@Autowired
	private BattleLogDrawerService battleLogDrawerService;
	@Autowired
	private SectorDrawerService sectorDrawerService;
	@Autowired
	private SupplyDrawerService supplyDrawerService;
	@Autowired
	private UnitDrawerService unitDrawerService;

	public void drawReach(GC gc, Set<WorldSector> sectors) {
		for (WorldSector sector : sectors) {
			mapDrawer.drawSquare(
					gc, 
					sector.getCoords(),
					imageLibrary.getShadedImage(sector.isCanReach(),
							sector.isEnoughMoves(), sector.isInSupply()));
		}
	}

	public Supply buildMapWithGC(GC gc) {
		sectorDrawerService.drawSectors(db.getBoardSize(), gc);
		// TODO GUI redraw if we auto switch off of battletabselected (e.g. by
		// move
		// or attack--see mapcanvascontrol)
		if (widgetContainer.getTabControl().battleTabSelected()) {
			for (BattleLogEntry entry : db.getBattleLogList()) {
				battleLogDrawerService.drawDestroyedUnit(gc, entry);
			}
		} else if (widgetContainer.getTabControl().supplyTabSelected()) {
			worldSupplyDisplayerService.displayWorldSupply(gc);
		}

		Supply supply = new Supply(db.getWorld());

		for (Unit unit : db.getUnitList()) {
			unitDrawerService.drawUnitDamage(gc, unit);
			supplyDrawerService.drawSupplyBG(gc, unit, supply);
		}
		for (Unit unit : db.getSeenUnitList()) {
			if (db.getWorld().getWorldSector(unit.getCoords()) == null) {
				continue;
			}
			unitDrawerService.drawUnitDamage(gc, unit);
		}
		return supply;
	}
}
