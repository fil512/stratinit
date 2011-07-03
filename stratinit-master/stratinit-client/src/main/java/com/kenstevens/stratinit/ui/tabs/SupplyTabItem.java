package com.kenstevens.stratinit.ui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class SupplyTabItem extends Composite {
	private Table agenda;
	private Table units;
	private TableColumn eventsTableColumn;

	public SupplyTabItem(Composite parent, int style) {
		super(parent, style);
		createContents();
	}

	private void createContents() {
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setBounds(10, 10, 92, 15);
		lblNewLabel.setText("");
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
}
