package com.kenstevens.stratinit.ui.window.map;

import com.google.common.collect.Sets;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.supply.Supply;
import com.kenstevens.stratinit.supply.SupplyTree;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoordVector;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.ui.image.ImageLibrary;
import com.kenstevens.stratinit.ui.window.LineStyle;
import org.eclipse.swt.graphics.GC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Service
public class SupplyDrawerService {
	@Autowired
	ImageLibrary imageLibrary;
	@Autowired
	MapDrawerService mapDrawer;
	@Autowired
	Data db;
	
	public void displayWorldSupply(GC gc) {
		drawPorts(gc);
		drawSupplyShips(gc);
	}

	private void drawSupplyShips(GC gc) {
		Set<SectorCoords> ships = Sets.newHashSet();
		// TODO REF use guava
		for (UnitView unit : db.getUnitList()) {
			if (unit.isSuppliesNavy()) {
				ships.add(unit.getCoords());
			}
		}
		// TODO REF use guava
		for (SectorCoords supplyCoords : ships) {
			for (SectorCoords coords : expand(Sets.newHashSet(supplyCoords))) {
		mapDrawer.drawSquare(gc, coords,
				imageLibrary.getSupplyImage());
			}
		}
		// TODO REF use guava
		for (SectorCoords supplyCoords : ships) {
			for (SectorCoords coords : expand(Sets.newHashSet(supplyCoords))) {
				WorldSector worldSector = db.getWorld().getWorldSectorOrNull(coords);
				if (worldSector != null && worldSector.isSuppliesNavy() && !supplyCoords.equals(coords)) {
					mapDrawer.drawLine(gc, supplyCoords, coords, LineStyle.SUPPLY_ISOLATED);
				}
			}
		}
	}

	private void drawPorts(GC gc) {
		Set<SectorCoords> ports = Sets.newHashSet();
		// TODO REF use guava
		for (CityView city : db.getCityList()) {
			if (city.isBase() || city.isPort()) {
				ports.add(city.getCoords());
			}
		}

		for (SectorCoords coords : expand(ports)) {
			mapDrawer.drawSquare(gc, coords,
					imageLibrary.getSupplyImage());
		}
	}

	private Set<SectorCoords> expand(Set<SectorCoords> suppliers) {
		Set<SectorCoords> retval = new HashSet<SectorCoords>();
		int size = db.getWorld().size();
		for (SectorCoords supplier : suppliers) {
            retval.addAll(supplier.sectorsWithin(size,
                    Constants.SUPPLY_RADIUS, true));
        }
		return retval;
	}
	
	void drawSupplyTree(Supply supply, Unit unit) {
		SupplyTree supplyTree = new SupplyTree(supply.getWorldView(), unit);
		Iterator<SectorCoordVector> iterator = supplyTree.getSupplyChain();
		while (iterator.hasNext()) {
			SectorCoordVector vector = iterator.next();
			LineStyle lineStyle;
			if (supplyTree.isLinkedToPort()) {
				lineStyle = LineStyle.SUPPLY_LINKED;
			} else {
				lineStyle = LineStyle.SUPPLY_ISOLATED;
			}
			mapDrawer.drawLine(vector.getStart(), vector.getEnd(), lineStyle);
		}
	}

	public void drawSupplyBG(GC gc, Unit unit, Supply supply) {
		SectorCoords coords = unit.getCoords();

		if (unit.isLand() || unit.isNavy()) {
			if (!supply.inSupply(unit)) {
				mapDrawer.drawSquare(gc, coords, imageLibrary.getOutOfSupply());
			} else if (!unit.atMaxAmmo()) {
				mapDrawer.drawSquare(gc, coords, imageLibrary.getLowAmmo());
			}
		}
	}


}
