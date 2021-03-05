package com.kenstevens.stratinit.client.control;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.client.control.selection.SelectEvent;
import com.kenstevens.stratinit.client.control.selection.SelectSectorEvent;
import com.kenstevens.stratinit.client.control.selection.SelectUnitsEvent;
import com.kenstevens.stratinit.client.control.selection.Selection.Source;
import com.kenstevens.stratinit.client.event.StratinitEventBus;
import com.kenstevens.stratinit.client.event.UnitListArrivedEvent;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.client.shell.ColourMap;
import com.kenstevens.stratinit.move.WorldView;
import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.*;

@Scope("prototype")
@Component
public class UnitTableControl implements Controller {
	private final Table table;
	@Autowired
	private SelectEvent selection;
	@Autowired
	private Data db;
	@Autowired
	private SelectedCoords selectedCoords;
	@Autowired
	private SelectedUnits selectedUnits;
	@Autowired
	protected StratinitEventBus eventBus;
	private final Predicate<Unit> unitFilter;
	private final boolean listenToSectorSelects;
	private final boolean isShowCoords;
	private final Comparator<UnitView> unitComparator;

	public UnitTableControl(Table unitTable, Predicate<Unit> unitFilter, Comparator<UnitView> unitComparator,
							boolean listenToSectorSelects, boolean isShowCoords) {
		this.unitFilter = unitFilter;
		this.unitComparator = unitComparator;
		this.listenToSectorSelects = listenToSectorSelects;
		this.table = unitTable;
		this.isShowCoords = isShowCoords;

		setTableListeners();
	}

	private final class TableListener implements Listener {
		private final Listener labelListener;
		private final Display display;
		private Shell tip = null;
		private Label label = null;

		private TableListener(Listener labelListener, Display display) {
			this.labelListener = labelListener;
			this.display = display;
		}

		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.Dispose:
			case SWT.KeyDown:
			case SWT.MouseMove: {
				if (tip == null)
					break;
				tip.dispose();
				tip = null;
				label = null;
				break;
			}
			case SWT.MouseHover: {
				TableItem item = table.getItem(new Point(event.x, event.y));
				if (item != null && item.getData() instanceof Unit) {
					Unit unit = (Unit) item.getData();
					Nation unitNation = unit.getNation();
					if (unitNation.equals(db.getNation())) {
						return;
					}
					popUpUnitNation(item, unitNation);
				}
			}
			}
		}

		private void popUpUnitNation(TableItem item, Nation unitNation) {
			if (tip != null && !tip.isDisposed())
				tip.dispose();
			tip = new Shell(table.getShell(), SWT.ON_TOP | SWT.TOOL);
			tip.setLayout(new FillLayout());
			label = new Label(tip, SWT.NONE);
			label.setForeground(display
					.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
			label.setBackground(display
					.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			label.setData("_TABLEITEM", item);
			label.setText(unitNation.toString());
			label.addListener(SWT.MouseExit, labelListener);
			label.addListener(SWT.MouseDown, labelListener);
			Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			Rectangle rect = item.getBounds(0);
			Point pt = table.toDisplay(rect.x, rect.y);
			tip.setBounds(pt.x, pt.y, size.x, size.y);
			tip.setVisible(true);
		}
	}

	@Subscribe
	public void handleUnitListArrivedEvent(UnitListArrivedEvent event) {
		unitsChanged();
	}

	@Subscribe
	public void handleSelectSectorEvent(SelectSectorEvent event) {
		if (!listenToSectorSelects) {
			return;
		}
		coordsSelected();
	}
	
	@Subscribe
	public void handleSelectUnitsEvent(SelectUnitsEvent event) {
		coordsSelected();
		unitsSelected();
	}
	
	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		eventBus.register(this);
	}

	public final void setTableListeners() {

		table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				TableItem[] items = table.getSelection();
				if (items == null || items.length == 0) {
					return;
				}
				List<UnitView> units = new ArrayList<UnitView>();
				for (TableItem item : items) {
					units.add((UnitView) item.getData());
				}
				selection.selectUnits(units, Source.UNIT_TAB);
			}
		});

		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TableItem item = table.getItem(new Point(e.x, e.y));
				if (item == null) {
					return;
				}
				Unit unit = (Unit) item.getData();
				if (unit == null) {
					return;
				}
				selection.selectUnits(
						db.getUnitList().typedUnitsAt(unit.getType(),
								selectedCoords.getCoords()), Source.UNIT_TAB);
			}
		});

		final Display display = Display.getCurrent();
		// Disable native tooltip
		table.setToolTipText("");

		// Implement a "fake" tooltip
		final Listener labelListener = new Listener() {
			public void handleEvent(Event event) {
				Label label = (Label) event.widget;
				Shell shell = label.getShell();
				if (event.type == SWT.MouseDown) {
					Event e = new Event();
					e.item = (TableItem) label.getData("_TABLEITEM");
					// Assuming table is single select, set the selection as if
					// the mouse down event went through to the table
					table.setSelection(new TableItem[] { (TableItem) e.item });
					table.notifyListeners(SWT.Selection, e);
				} else if (event.type == SWT.MouseExit) {
					shell.dispose();
				}
			}
		};

		Listener tableListener = new TableListener(labelListener, display);
		table.addListener(SWT.Dispose, tableListener);
		table.addListener(SWT.KeyDown, tableListener);
		table.addListener(SWT.MouseMove, tableListener);
		table.addListener(SWT.MouseHover, tableListener);
	}

	private void unitsChanged() {
		Collection<UnitView> unitList = getFilteredUnits(db.getUnitList());

		List<Integer> remove = new ArrayList<Integer>();
		Set<Unit> found = new HashSet<Unit>();
		int index = 0;
		ETAHelper etaHelper = new ETAHelper(db);
		for (TableItem item : table.getItems()) {
			Unit unit = (Unit) item.getData();
			if (unitList.contains(unit)) {
				unitToItem(unit, item, unit.getNation().equals(db.getNation()),
						etaHelper);
				found.add(unit);
			} else {
				remove.add(index);
			}
			++index;
		}
		table.remove(ArrayUtils.toPrimitive(remove.toArray(new Integer[0])));
		for (Unit unit : unitList) {
			if (!(found.contains(unit))) {
				// TODO * deal with enemy unit list changing
				addUnitToTable(unit, true, etaHelper);
			}
		}
	}

	private Collection<UnitView> getFilteredUnits(UnitList unitList) {
		return Collections2.filter(unitList.getUnits(), unitFilter);
	}

	private void unitsSelected() {
		List<TableItem> selectedItems = new ArrayList<TableItem>();
		for (TableItem item : table.getItems()) {
			UnitView unit = (UnitView) item.getData();
			if (selectedUnits.contains(unit)) {
				selectedItems.add(item);
			}
		}
		table.setSelection(selectedItems.toArray(new TableItem[0]));
	}

	private void coordsSelected() {
		table.removeAll();
		addAllUnits();
		table.redraw();
	}

	private void addAllUnits() {
		addUnitsToTable(db.getUnitList(), true);
		addUnitsToTable(db.getSeenUnitList(), false);
	}

	private void addUnitsToTable(UnitList unitList, boolean myUnit) {

		List<UnitView> units = Lists.newArrayList(getFilteredUnits(unitList));
		Collections.sort(units, unitComparator);
		ETAHelper etaHelper = new ETAHelper(db);
		for (Unit unit : units) {
			addUnitToTable(unit, myUnit, etaHelper);
		}
	}

	private void addUnitToTable(Unit unit, boolean myUnit, ETAHelper etaHelper) {

		TableItem item = new TableItem(table, SWT.NONE);
		unitToItem(unit, item, myUnit, etaHelper);
		item.setData(unit);
		if (myUnit) {
			item.setForeground(ColourMap.BLACK);
		} else {
			item.setForeground(ColourMap.RED);
		}
	}

	private void unitToItem(Unit unit, TableItem item, boolean myUnit,
			ETAHelper etaHelper) {
		if (isShowCoords) {
			item.setText(new String[] {
					unit.getCoords().toString(),
					unit.getType().toString(),
					filter(myUnit,
							"" + unit.getMobility()
									+ (unit.getUnitMove() == null ? "" : "-")),
					filter(myUnit, unit.getAmmoString()), unit.getHpString(),
					filter(myUnit, isVulnerable(unit) ? "*" : ""),
					filter(myUnit, "" + etaHelper.getETA(unit)) });
		} else {
			item.setText(new String[] {
					unit.getType().toString(),
					filter(myUnit,
							"" + unit.getMobility()
							+ (unit.getUnitMove() == null ? "" : "-")),
							filter(myUnit, unit.getAmmoString()), unit.getHpString(),
							filter(myUnit, unit.getFuelString()),
							filter(myUnit, "" + etaHelper.getETA(unit)) });
		}
	}

	private boolean isVulnerable(Unit unit) {
		WorldView worldView = db.getWorld();
		WorldSector worldSector = worldView.getWorldSector(unit);
		return worldView.isVulnerable(worldSector);
	}

	private String filter(boolean myUnit, String string) {
		if (myUnit) {
			return string;
		} else {
			return "";
		}
	}

	public List<Unit> getSelectedUnits() {
		List<Unit> units = new ArrayList<Unit>();
		for (TableItem tableItem : table.getSelection()) {
			Unit unit = (Unit) tableItem.getData();
			units.add(unit);
		}
		return units;
	}
}
