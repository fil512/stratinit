package com.kenstevens.stratinit.ui.window.map;

import java.util.Set;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.control.selection.SelectEvent;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.SelectedUnits;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.Movement;
import com.kenstevens.stratinit.shell.WidgetContainer;
import com.kenstevens.stratinit.site.action.UnitMover;
import com.kenstevens.stratinit.supply.Supply;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.ui.image.ImageLibrary;
import com.kenstevens.stratinit.ui.window.LineStyle;
import com.kenstevens.stratinit.ui.window.MapImageManager;

@Service
public class UnitDrawerService {
	@Autowired
	SelectedUnits selectedUnits;
	@Autowired
	WidgetContainer widgetContainer;
	@Autowired
	SectorDrawerService sectorDrawer;
	@Autowired
	SupplyDrawerService supplyDrawer;
	@Autowired
	Data db;
	@Autowired
	ImageLibrary imageLibrary;
	@Autowired
	MapDrawerService mapDrawer;
	@Autowired
	SelectEvent selectEvent;
	@Autowired
	MapImageManager mapImageManager;
	
	void drawUnit(GC gc, WorldSector sector) {
		if (sector == null || sector.getCityType() != null) {
			return;
		}
		UnitType topUnitType = sector.getTopUnitType();
		if (topUnitType == null) {
			return;
		}
		Image image;
		if (sectorDrawer.isNationSelected(sector.getNation())) {
			image = imageLibrary.getDestroyed(topUnitType);
		} else {
			RelationType relationType = sector.getMyRelation();
			image = imageLibrary.getUnitImage(relationType, topUnitType);
		}
		if (image == null) {
			throw new IllegalStateException("Unable to find image for unit ["
					+ topUnitType + "]");
		}
		mapDrawer.drawSquare(gc, sector.getCoords(), image);
	}
	
	private void drawUnitMove() {
		for (Unit unit : selectedUnits) {
			if (unit.getUnitMove() != null) {
				drawUnitMove(unit);
			}
		}
	}
	
	public void drawSelectedUnitRange(Supply supply) {
		if (selectedUnits.isEmpty()) {
			return;
		}
		if (widgetContainer.getTabControl().supplyTabSelected()) {
			return;
		}
		UnitView longest = UnitMover.getMaxRange(selectedUnits);
		Movement movement = new Movement(longest, db.getWorld());
		Set<WorldSector> sectors = movement.canReach(supply.inSupply(longest));
		drawReach(sectors);
		supplyDrawer.drawSupplyTree(supply, longest);
		drawUnitMove();
	}

	private void drawReach(Set<WorldSector> sectors) {
		sectorDrawer.displayActiveLocation();
		mapDrawer.drawReach(sectors);
	}
	
	public void drawUnitDamage(GC gc, Unit unit) {
		SectorCoords coords = unit.getCoords();
		if (unit.atMaxHP()) {
			return;
		}
		if (unit.isHurt()) {
			mapDrawer.drawSquare(gc, coords, imageLibrary.getDamaged());
		} else if (unit.isScratched()) {
			mapDrawer.drawSquare(gc, coords, imageLibrary.getWounded());
		}
	}



	private void drawUnitMove(Unit unit) {
		mapDrawer.drawLine(unit.getCoords(), unit.getUnitMove().getCoords(),
				LineStyle.UNIT_MOVE);
	}


}
