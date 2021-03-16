package com.kenstevens.stratinit.ui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class BattleTabItem extends Composite {
	private Table battleLogTable;
	private Button updateButton;
	private StyledText messageText;
	private Button btnDownload;

	/**
	 * Create the composite
	 * @param tabFolder
	 * @param style
	 */
	public BattleTabItem(Composite parent, int style) {
		super(parent, style);
		createContents();
	}

	private void createContents() {
		setLayout(new FormLayout());

		battleLogTable = new Table(this, SWT.BORDER | SWT.MULTI
				| SWT.FULL_SELECTION);
		final FormData fdBattleTable = new FormData();
		fdBattleTable.top = new FormAttachment(0);
		fdBattleTable.right = new FormAttachment(100);
		fdBattleTable.left = new FormAttachment(0, 39);
		battleLogTable.setLayoutData(fdBattleTable);
		battleLogTable.setLinesVisible(true);
		battleLogTable.setHeaderVisible(true);

		final TableColumn dateTableColumn = new TableColumn(battleLogTable,
				SWT.NONE);
		dateTableColumn.setAlignment(SWT.RIGHT);
		dateTableColumn.setWidth(93);
		dateTableColumn.setText("when");

		final TableColumn whoColumnTableColumn = new TableColumn(
				battleLogTable, SWT.NONE);
		whoColumnTableColumn.setToolTipText("Opponent");
		whoColumnTableColumn.setWidth(81);
		whoColumnTableColumn.setText("who");

		TableColumn tblclmnWhat = new TableColumn(battleLogTable, SWT.NONE);
		tblclmnWhat.setToolTipText("Enemy unit / city");
		tblclmnWhat.setWidth(76);
		tblclmnWhat.setText("their");

		final TableColumn eventTableColumn = new TableColumn(battleLogTable,
				SWT.NONE);
		eventTableColumn.setWidth(55);
		eventTableColumn.setText("did");
		eventTableColumn.setToolTipText("* means I'm defending");

		final TableColumn newColumnTableColumn2 = new TableColumn(
				battleLogTable, SWT.NONE);
		newColumnTableColumn2.setToolTipText("My unit / city");
		newColumnTableColumn2.setWidth(109);
		newColumnTableColumn2.setText("my");

		updateButton = new Button(this, SWT.NONE);
		updateButton.setToolTipText("Get the latest Battle Log entries");
		fdBattleTable.left = new FormAttachment(updateButton, 0, SWT.LEFT);
		final FormData fdUpdateButton = new FormData();
		fdUpdateButton.bottom = new FormAttachment(100, -5);
		fdUpdateButton.right = new FormAttachment(0, 100);
		fdUpdateButton.left = new FormAttachment(0, 5);
		updateButton.setLayoutData(fdUpdateButton);
		updateButton.setText("Update");

		messageText = new StyledText(this, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL );
		fdBattleTable.bottom = new FormAttachment(messageText, -6);
		FormData fdMessageText = new FormData();
		fdMessageText.top = new FormAttachment(updateButton, -99, SWT.TOP);
		fdMessageText.bottom = new FormAttachment(updateButton, -6);
		fdMessageText.left = new FormAttachment(0, 5);
		fdMessageText.right = new FormAttachment(0, 440);
		messageText.setLayoutData(fdMessageText);

		btnDownload = new Button(this, SWT.NONE);
		FormData fdBtnDownload = new FormData();
		fdBtnDownload.top = new FormAttachment(updateButton, 0, SWT.TOP);
		fdBtnDownload.left = new FormAttachment(updateButton, 6);
		btnDownload.setLayoutData(fdBtnDownload);
		btnDownload.setText("Download");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public Table getBattleLogTable() {
		return battleLogTable;
	}

	public Button getUpdateButton() {
		return updateButton;
	}
	public StyledText getMessageText() {
		return messageText;
	}
	public Button getDownloadButton() {
		return btnDownload;
	}
}
