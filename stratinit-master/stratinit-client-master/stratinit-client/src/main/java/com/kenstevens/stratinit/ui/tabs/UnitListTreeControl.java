package com.kenstevens.stratinit.ui.tabs;

import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gwt.event.shared.HandlerManager;
import com.kenstevens.stratinit.control.Controller;
import com.kenstevens.stratinit.event.UnitListArrivedEvent;
import com.kenstevens.stratinit.event.UnitListArrivedEventHandler;
import com.kenstevens.stratinit.event.UnitListReplacementArrivedEvent;
import com.kenstevens.stratinit.event.UnitListReplacementArrivedEventHandler;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.LaunchedSatellite;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.ui.selection.SelectEvent;
import com.kenstevens.stratinit.ui.selection.SelectUnitsEvent;
import com.kenstevens.stratinit.ui.selection.SelectUnitsEventHandler;
import com.kenstevens.stratinit.ui.selection.Selection.Source;

@Scope("prototype")
@Component
public class UnitListTreeControl implements Controller {
	private final class TreeListener implements Listener {
		public void handleEvent(Event event) {
			TreeItem item = (TreeItem) event.item;
			if (item == null) {
				return;
			}
			Object data = item.getData();
			if (data == null) {
				return;
			}
			if (data instanceof UnitView) {
				UnitView unit = (UnitView) data;
				selectEvent.selectUnit(unit, Source.UNIT_TAB);
			} else if (data instanceof LaunchedSatellite) {
				LaunchedSatellite sat = (LaunchedSatellite) data;
				selectEvent.selectSectorCoords(sat.getCoords(), Source.UNIT_TAB);
			}
		}
	}

	private final Tree tree;
	private final TreeItemList treeItemList;
	private TreeItemListControl treeItemListControl;
	@Autowired
	private Data db;
	@Autowired
	private SelectEvent selectEvent;
	@Autowired
	private HandlerManager handlerManager;

	public UnitListTreeControl(UnitTabItem unitTabItem) {
		this.tree = unitTabItem.getUnitListTree();
		this.treeItemList = new TreeItemList(tree);
		setTreeListeners();
	}


	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		treeItemListControl = new TreeItemListControl(treeItemList, db);
		handlerManager.addHandler(UnitListArrivedEvent.TYPE,
				new UnitListArrivedEventHandler() {
					@Override
					public void dataArrived() {
						updateTree();
					}
				});
		
		handlerManager.addHandler(UnitListReplacementArrivedEvent.TYPE,
				new UnitListReplacementArrivedEventHandler() {
					@Override
					public void dataArrived() {
						rebuildTree();
					}
				});
		handlerManager.addHandler(SelectUnitsEvent.TYPE,
				new SelectUnitsEventHandler() {

					@Override
					public void selectUnits(Source source) {
						List<UnitView> units = selectEvent.getSelectedUnits();
						if (units.isEmpty()) {
							return;
						}
						Unit unit = units.get(0);
						select(unit);
					}
				});
		
	}

	public final void setTreeListeners() {
		tree.addListener(SWT.Selection, new TreeListener());
	}

	protected void select(Unit unit) {
		if (!treeItemListControl.contains(unit)) {
			return;
		}
		TreeItem tItem = treeItemListControl.get(unit.getUnitBase());
		if (tItem == null) {
			return;
		}
		TreeItem found = null;
		for (TreeItem unitItem : tItem.getItems()) {
			Unit treeUnit = (Unit) unitItem.getData();
			if (treeUnit == null) {
				for (TreeItem childItem : unitItem.getItems()) {
					Unit childUnit = (Unit) childItem.getData();
					if (childUnit != null && childUnit.equals(unit)) {
						found = childItem;
						break;
					}
				}
			} else if (treeUnit.equals(unit)) {
				found = unitItem;
			}
			if (found != null) {
				break;
			}
		}
		if (found != null) {
			tree.setSelection(found);
		}
	}

	private void expandTree() {
		for (TreeItem item : tree.getItems()) {
			item.setExpanded(true);
			for (TreeItem child : item.getItems()) {
				if (!(child.getText().equals(TreeItemList.MOVED))
						&& !(child.getText().equals(TreeItemList.HOLD))
						&& !(child.getText().equals(TreeItemList.AT_SEA))
						&& !(child.getText().equals(TreeItemList.BOARDING))) {
					child.setExpanded(true);
				}
			}
		}
	}


	private void updateTree() {
		List<UnitView> dbUnits = db.getUnitList().getUnits();
		WorldView worldView = db.getWorld();
		for (TreeItem item : tree.getItems()) {
			updateTreeItem(worldView, dbUnits, item);
		}
	}

	private void updateTreeItem(WorldView worldView, List<UnitView> dbUnits,
			TreeItem item) {
		if (item.getText().equals(TreeItemList.ORBIT)) {
			return;
		}
		UnitView listUnit = (UnitView) item.getData();
		if (listUnit == null) {
			// We're a header, recurse into children
			if (item.getItemCount() > 0) {
				for (TreeItem child : item.getItems()) {
					updateTreeItem(worldView, dbUnits, child);
				}
			}
		} else {
			if (dbUnits.contains(listUnit)) {
				item.setText(treeItemListControl.toStringArray(worldView, listUnit));
				treeItemListControl.setUnitTreeItemColour(listUnit, item);
			} else {
				// Unit has died
				listUnit.setAlive(false);
				item.setText(treeItemListControl.toStringArray(worldView, listUnit));
				treeItemListControl.updateHeader(db, item, listUnit);
			}
		}
	}


	public void rebuildTree() {
		treeItemListControl.clear();
		treeItemListControl.treeifyUnits();
		expandTree();
		treeItemListControl.addSatellites();
		tree.redraw();
	}
}
