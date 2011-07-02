package com.kenstevens.stratinit.ui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class GameTable extends Composite {
	private Table table;

	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public GameTable(Composite parent, int style) {
		super(parent, style);
		createContents();
	}

	private void createContents() {
		setLayout(new FillLayout(SWT.HORIZONTAL));
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnId = new TableColumn(table, SWT.NONE);
		tblclmnId.setWidth(41);
		tblclmnId.setText("Id");

		TableColumn tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(100);
		tblclmnName.setText("Name");

		TableColumn tblclmnSize = new TableColumn(table, SWT.NONE);
		tblclmnSize.setWidth(100);
		tblclmnSize.setText("Size");

		TableColumn tblclmnPlayers = new TableColumn(table, SWT.NONE);
		tblclmnPlayers.setWidth(100);
		tblclmnPlayers.setText("players");

		TableColumn tblclmnCreated = new TableColumn(table, SWT.NONE);
		tblclmnCreated.setWidth(121);
		tblclmnCreated.setText("created");

		TableColumn tblclmnOpens = new TableColumn(table, SWT.NONE);
		tblclmnOpens.setWidth(130);
		tblclmnOpens.setText("opens");

		TableColumn tblclmnStarts = new TableColumn(table, SWT.NONE);
		tblclmnStarts.setWidth(130);
		tblclmnStarts.setText("starts");

		TableColumn tblclmnEnds = new TableColumn(table, SWT.NONE);
		tblclmnEnds.setWidth(130);
		tblclmnEnds.setText("ends");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public TableItem[] getSelection() {
		return table.getSelection();
	}

	public Table getTable() {
		return table;
	}
}
