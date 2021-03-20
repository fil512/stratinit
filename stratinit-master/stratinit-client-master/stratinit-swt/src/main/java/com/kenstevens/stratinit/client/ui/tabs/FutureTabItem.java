package com.kenstevens.stratinit.client.ui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class FutureTabItem extends Composite {
	private Table agenda;
	private Table units;
	private TableColumn eventsTableColumn;

	public FutureTabItem(Composite parent, int style) {
		super(parent, style);
		createContents();
	}

	private void createContents() {
		setLayout(new FormLayout());

		agenda = new Table(this, SWT.BORDER | SWT.FULL_SELECTION| SWT.NO_SCROLL | SWT.V_SCROLL);
		final FormData fdAgenda = new FormData();
		fdAgenda.bottom = new FormAttachment(0, 235);
		fdAgenda.right = new FormAttachment(100, -5);
		fdAgenda.left = new FormAttachment(0, 2);
		fdAgenda.top = new FormAttachment(0, 5);
		agenda.setLayoutData(fdAgenda);
		agenda.setLinesVisible(true);
		agenda.setHeaderVisible(true);

		final TableColumn timeTableColumn = new TableColumn(agenda, SWT.RIGHT);
		timeTableColumn.setAlignment(SWT.RIGHT);
		timeTableColumn.setWidth(76);
		timeTableColumn.setText("Time");

		eventsTableColumn = new TableColumn(agenda, SWT.NONE);
		eventsTableColumn.setWidth(317);
		eventsTableColumn.setText("Events");

		units = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		FormData fdUnits = new FormData();
		fdUnits.bottom = new FormAttachment(100, -5);
		fdUnits.right = new FormAttachment(100, -5);
		fdUnits.left = new FormAttachment(0, 2);
		fdUnits.top = new FormAttachment(agenda, 5, SWT.BOTTOM);
		units.setLayoutData(fdUnits);
		units.setLinesVisible(true);
		units.setHeaderVisible(true);

		final TableColumn timTableColumn = new TableColumn(units, SWT.NONE);
		timTableColumn.setWidth(76);
		timTableColumn.setText("Time");

		TableColumn xyTableColumn = new TableColumn(units, SWT.NONE);
		xyTableColumn.setWidth(50);
		xyTableColumn.setText("x,y");

		final TableColumn typeTableColumn = new TableColumn(units, SWT.NONE);
		typeTableColumn.setWidth(108);
		typeTableColumn.setText("Type");

		final TableColumn newColumnTableColumn = new TableColumn(units, SWT.NONE);
		newColumnTableColumn.setWidth(57);
		newColumnTableColumn.setText("moves");

		final TableColumn eventColumn = new TableColumn(units, SWT.NONE);
		eventColumn.setWidth(106);
		eventColumn.setText("Event");
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
