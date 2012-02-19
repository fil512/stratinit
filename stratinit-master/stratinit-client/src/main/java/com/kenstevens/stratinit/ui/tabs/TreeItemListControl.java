package com.kenstevens.stratinit.ui.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.LaunchedSatellite;
import com.kenstevens.stratinit.model.SatelliteList;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.Movement;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.util.ContainerUnit;
import com.kenstevens.stratinit.util.UpdateManager;

public class TreeItemListControl {
	private final Data db;
	private final TreeItemList treeItemList;
	private final Map<UnitBase, TreeItem> unitTypeItemMap = new HashMap<UnitBase, TreeItem>();
	private final Set<Unit> unitsInTree = new HashSet<Unit>();

	public TreeItemListControl(TreeItemList treeItemList, Data db) {
		this.treeItemList = treeItemList;
		this.db = db;
	}

	void treeifyUnits() {
		WorldView worldView = db.getWorld();
		TreeUnitsByType treeUnitsByType = new TreeUnitsByType(db);

		for (UnitType type : treeUnitsByType.keySet()) {
			UnitBase unitBase = UnitBase.getUnitBase(type);
			TreeItem tItem;
			if (unitBase.isAir()) {
				tItem = createUnitTypeItem(unitBase, treeItemList.getAirItem());
			} else if (unitBase.isNavy()) {
				tItem = createUnitTypeItem(unitBase, treeItemList.getNavyItem());
			} else if (unitBase.isLand()) {
				tItem = createUnitTypeItem(unitBase, treeItemList.getLandItem());
			} else if (unitBase.isTech()) {
				tItem = createUnitTypeItem(unitBase, treeItemList.getTechItem());
			} else {
				throw new IllegalStateException("Bad unit");
			}
			updateUnitTypeItem(treeUnitsByType.count(type), type, tItem);
			List<UnitView> movedUnits = new ArrayList<UnitView>();
			List<UnitView> repairingUnits = new ArrayList<UnitView>();
			List<UnitView> strandedUnits = new ArrayList<UnitView>();
			List<UnitView> holdUnits = new ArrayList<UnitView>();
			List<UnitView> atSeaUnits = new ArrayList<UnitView>();
			List<UnitView> boardingUnits = new ArrayList<UnitView>();
			for (UnitView unit : treeUnitsByType.get(type)) {
				// TODO REF check earlier whether world is loaded
				if (db.getWorld().getWorldSector(unit) == null) {
					break;
				}
				Movement movement = new Movement(unit, db.getWorld());
				if (unit.isDamaged() && movement.isHealing()) {
					repairingUnits.add(unit);
				} else if (unit.isLand() && movement.isAtSea()) {
					atSeaUnits.add(unit);
				} else if (unit.isLand() && movement.isBoarding()) {
					boardingUnits.add(unit);
				} else if (unit.getMobility() <= 1) {
					movedUnits.add(unit);
				} else if (unit.isNew() || unit.isMoveIncreased()) {
					addUnitToTree(worldView, tItem, unit);
				} else if (unit.isLand() && (unit.atMaxHP())
						&& !movement.isOnCoast()) {
					holdUnits.add(unit);
				} else if ((unit.isSupply() || unit.isZeppelin())
						&& unit.atMaxMobility()) {
					holdUnits.add(unit);
				} else {
					addUnitToTree(worldView, tItem, unit);
				}
			}
			createSubTree(worldView, tItem, holdUnits, TreeItemList.HOLD);
			createSubTree(worldView, tItem, boardingUnits, TreeItemList.BOARDING);
			createSubTree(worldView, tItem, atSeaUnits, TreeItemList.AT_SEA);
			createSubTree(worldView, tItem, repairingUnits, TreeItemList.REPAIRING);
			createSubTree(worldView, tItem, movedUnits, TreeItemList.MOVED);
			createSubTree(worldView, tItem, strandedUnits, TreeItemList.STRANDED);
		}

	}
	
	private TreeItem createUnitTypeItem(UnitBase unitType, TreeItem parent) {
		TreeItem tItem = unitTypeItemMap.get(unitType);
		if (tItem == null) {
			tItem = new TreeItem(parent, 0);
			tItem.setExpanded(true);
			unitTypeItemMap.put(unitType, tItem);
		}
		return tItem;
	}

	private void updateUnitTypeItem(int count,
			UnitType type, TreeItem tItem) {
		tItem.setText(new String[] { type.toString(),
				"" + count });
		tItem.setFont(treeItemList.getBoldTreeFont());
	}

	private void createSubTree(WorldView worldView, TreeItem tItem,
			List<UnitView> units, String heading) {
		if (units.isEmpty()) {
			return;
		}
		TreeItem treeItem = new TreeItem(tItem, 0);
		treeItem.setText(heading);
		for (UnitView unit : units) {
			addUnitToTree(worldView, treeItem, unit);
		}
	}


	private void addUnitToTree(WorldView worldView, TreeItem tItem,
			UnitView unit) {
		TreeItem uitem = new TreeItem(tItem, 0);
		uitem.setText(toStringArray(worldView, unit));
		setUnitTreeItemColour(unit, uitem);
		uitem.setData(unit);
		unitsInTree.add(unit);
	}

	void setUnitTreeItemColour(UnitView unit, TreeItem uitem) {
		if (unit.isInActionQueue()) {
			uitem.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_GRAY));
		} else if (unit.isNew()) {
			uitem.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_DARK_GREEN));
		} else if (unit.isMoveIncreased()) {
			uitem.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_RED));
		} else {
			uitem.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_BLACK));
		}
	}



	void addSatellites() {
		TreeItem orbitItem = treeItemList.getOrbitItem();
		orbitItem.removeAll();
		SatelliteList satellites = db.getSatelliteList();
		for (LaunchedSatellite sat : satellites) {
			TreeItem uitem = new TreeItem(orbitItem, 0);
			uitem.setText(toStringArray(sat));
			uitem.setData(sat);
		}
	}

	public TreeItem get(UnitBase unitBase) {
		return unitTypeItemMap.get(unitBase);
	}
	
	public String[] toStringArray(WorldView worldView, Unit unit) {
		List<String> list = new ArrayList<String>();
		String type = "";
		WorldSector worldSector = worldView.getWorldSector(unit);
		if (worldSector != null) {
			if (worldView.isVulnerable(worldSector)) {
				type = "*";
			} else if (unit.carriesUnits() && worldSector.isWater()) {
				List<Unit> units = new ArrayList<Unit>();
				for (UnitView unitView : db.getUnitList().unitsAt(
						unit.getCoords())) {
					units.add(unitView);
				}
				List<Unit> passengers = new ContainerUnit(unit, units)
						.getPassengers(worldSector);
				if (!passengers.isEmpty()) {
					type = "" + passengers.size();
				}
			}
		}
		list.add(type);
		if (unit.isAlive()) {
			list.add("" + unit.getCoords());
			list.add("" + unit.getMobility() + (unit.getUnitMove() == null ? "" : "-"));
			list.add("" + unit.getAmmo());
			list.add("" + unit.getHp());
			list.add("" + unit.getFuel());
		} else {
			list.add("X,X");
			list.add("-");
			list.add("-");
			list.add("-");
			list.add("-");
		}
		list.add(new UpdateManager(unit).getShortETA());
		return list.toArray(new String[0]);
	}

	public String[] toStringArray(LaunchedSatellite sat) {
		List<String> list = new ArrayList<String>();
		list.add("" + sat.getCoords());
		list.add("-");
		list.add("-");
		list.add("-");
		list.add("-");
		list.add("-");
		return list.toArray(new String[0]);
	}

	public void clear() {
		treeItemList.clear();
		unitTypeItemMap.clear();
		treeItemList.addHeaders();
		unitsInTree.clear();
	}

	public boolean contains(Unit unit) {
		return unitsInTree.contains(unit);
	}

	public void updateHeader(Data db, TreeItem item, UnitView listUnit) {
		TreeItem typeItem = unitTypeItemMap.get(listUnit.getUnitBase());
		if (typeItem == null) {
			return;
		}
		UnitType type = listUnit.getType();
		// TODO REF is there a way we could reuse this?
		TreeUnitsByType treeUnitsByType = new TreeUnitsByType(db);
		int count = treeUnitsByType.count(type);
		updateUnitTypeItem(count , type, typeItem);
	}
}
