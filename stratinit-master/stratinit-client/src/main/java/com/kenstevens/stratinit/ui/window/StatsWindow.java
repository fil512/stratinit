package com.kenstevens.stratinit.ui.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.stats.Stats;
import com.kenstevens.stratinit.ui.shell.Window;

@Component
public class StatsWindow implements Window {
	private Table opponentsTable;
	private Table detailsTable;
	private Combo combo;
	private Table unitTable;
	private Shell dialog;
	@Autowired
	private Stats stats;

	/**
	 * Open the window
	 */
	/**
	 * @wbp.parser.entryPoint
	 */
	public void open(Shell shell) {
		dialog = new Shell(shell);
		dialog.setLayout(new FillLayout());
		createContents();
		setComboListeners();
		dialog.open();
	}

	private void setComboListeners() {
		combo.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				selectOpponent();
			}
		});
		combo.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event e) {
				selectOpponent();
			}
		});
	}

	private void createContents() {
		dialog.setSize(526, 506);
		dialog.setText("Stats");

		final TabFolder tabFolder = new TabFolder(dialog, SWT.NONE);

		final TabItem opponentsTabItem1 = new TabItem(tabFolder, SWT.NONE);
		opponentsTabItem1.setText("Opponents");

		opponentsTable = new Table(tabFolder, SWT.BORDER);
		opponentsTable.setLinesVisible(true);
		opponentsTable.setHeaderVisible(true);
		opponentsTabItem1.setControl(opponentsTable);

		final TableColumn opponentColumnTableColumn = new TableColumn(opponentsTable, SWT.NONE);
		opponentColumnTableColumn.setWidth(100);
		opponentColumnTableColumn.setText("Opponent");

		final TableColumn iLostTableColumn = new TableColumn(opponentsTable, SWT.NONE);
		iLostTableColumn.setToolTipText("Total hours to build units lost");
		iLostTableColumn.setWidth(100);
		iLostTableColumn.setText("I lost");

		final TableColumn iKilledColumnTableColumn = new TableColumn(opponentsTable, SWT.NONE);
		iKilledColumnTableColumn.setToolTipText("Total hours to build units killed");
		iKilledColumnTableColumn.setWidth(100);
		iKilledColumnTableColumn.setText("I killed");

		final TableColumn netColumnTableColumn = new TableColumn(opponentsTable, SWT.NONE);
		netColumnTableColumn.setWidth(100);
		netColumnTableColumn.setText("Net");

		final TableColumn dominanceColumnTableColumn = new TableColumn(opponentsTable, SWT.NONE);
		dominanceColumnTableColumn.setToolTipText("I killed / (I lost + I killed)");
		dominanceColumnTableColumn.setWidth(100);
		dominanceColumnTableColumn.setText("Dominance");

		final TabItem detailsTabItem = new TabItem(tabFolder, SWT.NONE);
		detailsTabItem.setText("Details");

		final Composite opponentComposite = new Composite(tabFolder, SWT.NONE);
		opponentComposite.setLayout(new FormLayout());
		detailsTabItem.setControl(opponentComposite);

		combo = new Combo(opponentComposite, SWT.NONE);
		final FormData fdCombo = new FormData();
		fdCombo.top = new FormAttachment(0, 10);
		fdCombo.right = new FormAttachment(0, 250);
		fdCombo.left = new FormAttachment(0, 90);
		combo.setLayoutData(fdCombo);

		Label opponentLabel;
		opponentLabel = new Label(opponentComposite, SWT.NONE);
		final FormData fdOpponentLabel = new FormData();
		fdOpponentLabel.top = new FormAttachment(combo, 0, SWT.TOP);
		fdOpponentLabel.left = new FormAttachment(0, 10);
		opponentLabel.setLayoutData(fdOpponentLabel);
		opponentLabel.setText("Opponent:");

		detailsTable = new Table(opponentComposite, SWT.BORDER);
		final FormData fdTable = new FormData();
		fdTable.left = new FormAttachment(0, 5);
		fdTable.bottom = new FormAttachment(100, -5);
		fdTable.right = new FormAttachment(100, -5);
		fdTable.top = new FormAttachment(0, 55);
		detailsTable.setLayoutData(fdTable);
		detailsTable.setLinesVisible(true);
		detailsTable.setHeaderVisible(true);

		final TableColumn unitColumnTableColumn = new TableColumn(detailsTable,
				SWT.NONE);
		unitColumnTableColumn.setWidth(138);
		unitColumnTableColumn.setText("Unit");

		final TableColumn unitLostColumnTableColumn = new TableColumn(detailsTable,
				SWT.NONE);
		unitLostColumnTableColumn.setWidth(82);
		unitLostColumnTableColumn.setText("I Lost");

		final TableColumn unitKilledColumnTableColumn = new TableColumn(detailsTable,
				SWT.NONE);
		unitKilledColumnTableColumn.setWidth(90);
		unitKilledColumnTableColumn.setText("I Killed");

		final TableColumn unitNetColumnTableColumn = new TableColumn(detailsTable,
				SWT.NONE);
		unitNetColumnTableColumn.setWidth(100);
		unitNetColumnTableColumn.setText("Net");

		final TableColumn netCostColumnTableColumn = new TableColumn(detailsTable, SWT.NONE);
		netCostColumnTableColumn.setToolTipText("Total cost in hours to make the units");
		netCostColumnTableColumn.setWidth(100);
		netCostColumnTableColumn.setText("Net Cost");

		final TabItem unitsTabItem = new TabItem(tabFolder, SWT.NONE);
		unitsTabItem.setText("Units");

		final Composite unitComposite = new Composite(tabFolder, SWT.NONE);
		unitComposite.setLayout(new FillLayout());
		unitsTabItem.setControl(unitComposite);

		unitTable = new Table(unitComposite, SWT.BORDER);
		unitTable.setLinesVisible(true);
		unitTable.setHeaderVisible(true);

		final TableColumn newColumnTableColumn = new TableColumn(unitTable,
				SWT.NONE);
		newColumnTableColumn.setWidth(100);
		newColumnTableColumn.setText("Unit");

		final TableColumn buildColumnTableColumn = new TableColumn(unitTable,
				SWT.NONE);
		buildColumnTableColumn.setWidth(100);
		buildColumnTableColumn.setText("Built");

		final TableColumn unitsLostColumnTableColumn = new TableColumn(unitTable,
				SWT.NONE);
		unitsLostColumnTableColumn.setWidth(100);
		unitsLostColumnTableColumn.setText("Lost");

		final TableColumn standingColumnTableColumn = new TableColumn(unitTable,
				SWT.NONE);
		standingColumnTableColumn.setWidth(100);
		standingColumnTableColumn.setText("Standing");

		final TableColumn preservationColumnTableColumn = new TableColumn(unitTable,
				SWT.NONE);
		preservationColumnTableColumn.setWidth(100);
		preservationColumnTableColumn.setText("Preservation");
	}

	public void setContents(int gameId) {
		stats.update(gameId);
		for (String[] unitRow : stats.getUnitRecordStats()) {
			TableItem item = new TableItem(unitTable, SWT.NONE);
			item.setText(unitRow);
		}
		for (String[] opponentRow : stats.getOpponentStats()) {
			TableItem item = new TableItem(opponentsTable, SWT.NONE);
			item.setText(opponentRow);
		}

		for (String opponent : stats.getOpponents()) {
			combo.add(opponent);
		}
	}

	private void selectOpponent() {
		String opponent = combo.getText();
		if (opponent == null) {
			return;
		}
		detailsTable.removeAll();
		for (String[] opponentRow : stats.getOpponentStats(opponent)) {
			TableItem item = new TableItem(detailsTable, SWT.NONE);
			item.setText(opponentRow);
		}
	}
}
