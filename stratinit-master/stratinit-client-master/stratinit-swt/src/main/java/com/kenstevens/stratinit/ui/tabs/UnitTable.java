package com.kenstevens.stratinit.ui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class UnitTable extends Composite {
	private final Table table;
	private final boolean showCoords;

	public UnitTable(Composite parent, int style, boolean showCoords) {
		super(parent, style);
		this.showCoords = showCoords;
		setLayout(new FillLayout());
		table = new Table(this, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION
				| SWT.NO_SCROLL | SWT.V_SCROLL);

		if (isShowCoords()) {
			final TableColumn coordsColumn = new TableColumn(table, SWT.NONE);
			coordsColumn.setWidth(50);
			coordsColumn.setText("x,y");
		}
		final TableColumn typeColumn = new TableColumn(table, SWT.NONE);
		if (isShowCoords()) {
			typeColumn.setWidth(80);
		} else {
			typeColumn.setWidth(130);
		}
		typeColumn.setText("type");

		final TableColumn movesColumn = new TableColumn(table, SWT.NONE);
		movesColumn
				.setToolTipText("Moves Remaining (\"-\" indicates move order)");
		movesColumn.setWidth(47);
		movesColumn.setText("mob");

		TableColumn tblclmnAmmo = new TableColumn(table, SWT.RIGHT);
		tblclmnAmmo.setWidth(54);
		tblclmnAmmo.setText("ammo");

		final TableColumn hpColumn = new TableColumn(table, SWT.NONE);
		hpColumn.setToolTipText("Hit Points");
		hpColumn.setWidth(33);
		hpColumn.setText("hp");

		final TableColumn fuelColumn = new TableColumn(table, SWT.NONE);
		fuelColumn.setWidth(41);
		if (showCoords) {
			fuelColumn.setText("port");
		} else {
			fuelColumn.setText("fuel");
		}

		final TableColumn hoursColumn = new TableColumn(table, SWT.NONE);
		hoursColumn.setToolTipText("Next Move / Repair Time");
		hoursColumn.setWidth(62);
		hoursColumn.setText("ETA");

		table.setLinesVisible(true);
		table.setHeaderVisible(true);
	}

	public Table getTable() {
		return table;
	}

	public boolean isShowCoords() {
		return showCoords;
	}
}
