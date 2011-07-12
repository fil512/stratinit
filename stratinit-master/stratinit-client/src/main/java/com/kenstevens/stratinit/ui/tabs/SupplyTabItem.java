package com.kenstevens.stratinit.ui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.layout.FillLayout;

public class SupplyTabItem extends Composite {
	private Table agenda;
	private Table units;
	private TableColumn eventsTableColumn;
	private UnitTable unitTable;

	public SupplyTabItem(Composite parent, int style) {
		super(parent, style);
		createContents();
	}

	private void createContents() {
		setLayout(new FillLayout());
		this.unitTable = new UnitTable(this, SWT.NONE, true);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public Table getAgenda() {
		return agenda;
	}

	public Table getUnits() {
		return units;
	}

	public TableColumn getEventsTableColumn() {
		return eventsTableColumn;
	}

	public UnitTable getUnitTable() {
		return unitTable;
	}
}
