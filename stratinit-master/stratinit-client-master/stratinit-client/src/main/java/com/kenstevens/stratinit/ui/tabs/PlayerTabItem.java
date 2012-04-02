package com.kenstevens.stratinit.ui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class PlayerTabItem extends Composite {
	private Table playerTable;
	private Button refreshButton;
	private Text theirRelationText;
	private Text switchesText;
	private Text nextRelation;
	private Text theirSwitchesText;
	private Text theirNextRelationText;
	private Combo yourRelationCombo;
	private Table teamTable;

	/**
	 * Create the composite
	 * 
	 * @param tabFolder
	 * @param style
	 */
	public PlayerTabItem(Composite parent, int style) {
		super(parent, style);
		createContents();
	}

	private void createContents() {
		setLayout(new FormLayout());
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent e) {
			}
		});

		Label teamsLabel = new Label(this, SWT.NONE);
		FormData fdTeamsLabel = new FormData();
		fdTeamsLabel.top = new FormAttachment(0, 2);
		fdTeamsLabel.left = new FormAttachment(0, 2);
		teamsLabel.setLayoutData(fdTeamsLabel);
		teamsLabel.setText("Teams:");

		teamTable = new Table(this, SWT.BORDER | SWT.HIDE_SELECTION);
		FormData fdTeamTable = new FormData();
		fdTeamTable.right = new FormAttachment(0, 414);
		fdTeamTable.bottom = new FormAttachment(30);
		fdTeamTable.top = new FormAttachment(teamsLabel, 1);
		fdTeamTable.left = new FormAttachment(0);
		teamTable.setLayoutData(fdTeamTable);
		teamTable.setHeaderVisible(true);
		teamTable.setLinesVisible(true);

		// DISABLE selection in the team table
		teamTable.addListener(SWT.EraseItem, new Listener() {
			public void handleEvent(Event event) {
				if ((event.detail & SWT.SELECTED) != 0) {
					event.detail &= ~SWT.SELECTED;
					TableItem item = (TableItem) event.item;
					Rectangle r = item.getBounds(event.index);
					event.gc.setBackground(item.getBackground(event.index));
					event.gc.setForeground(item.getForeground(event.index));
					event.gc.drawText(" ", r.x, r.y, false);
				}
			}
		});

		TableColumn tblclmnPlayer = new TableColumn(teamTable, SWT.NONE);
		tblclmnPlayer.setWidth(165);
		tblclmnPlayer.setText("player");

		TableColumn tblclmnPlayer1 = new TableColumn(teamTable, SWT.NONE);
		tblclmnPlayer1.setWidth(165);
		tblclmnPlayer1.setText("player");

		TableColumn tblclmnScore = new TableColumn(teamTable, SWT.NONE);
		tblclmnScore.setToolTipText("Total number of cities");
		tblclmnScore.setWidth(75);
		tblclmnScore.setText("score");

		playerTable = new Table(this, SWT.BORDER | SWT.SINGLE
				| SWT.FULL_SELECTION);
		FormData fdPlayerTable = new FormData();
		fdPlayerTable.right = new FormAttachment(0, 409);
		fdPlayerTable.left = new FormAttachment(0);
		playerTable.setLayoutData(fdPlayerTable);
		playerTable.setLinesVisible(true);
		playerTable.setHeaderVisible(true);
		playerTable.setBounds(0, 10, 401, 229);

		final TableColumn loggedOnColumn = new TableColumn(playerTable,
				SWT.NONE);
		loggedOnColumn.setToolTipText("player is logged on");
		loggedOnColumn.setWidth(23);
		loggedOnColumn.setText("*");

		final TableColumn myColumnTableColumn = new TableColumn(playerTable,
				SWT.NONE);
		myColumnTableColumn
				.setToolTipText("My Relation: A = ally, F = friendly, N = NAP, W = war");
		myColumnTableColumn.setWidth(31);
		myColumnTableColumn.setText("M");

		final TableColumn theirColumnTableColumn = new TableColumn(playerTable,
				SWT.NONE);
		theirColumnTableColumn
				.setToolTipText("Their Relation: A = ally, F = friendly, N = NAP, W = war");
		theirColumnTableColumn.setWidth(31);
		theirColumnTableColumn.setText("T");

		final TableColumn nameColumnTableColumn = new TableColumn(playerTable,
				SWT.NONE);
		nameColumnTableColumn.setWidth(128);
		nameColumnTableColumn.setText("name");

		final TableColumn citiesColumnTableColumn = new TableColumn(
				playerTable, SWT.NONE);
		citiesColumnTableColumn.setToolTipText("Number of Cities Owned");
		citiesColumnTableColumn.setWidth(34);
		citiesColumnTableColumn.setText("cit");

		final TableColumn powerColumnTableColumn = new TableColumn(playerTable,
				SWT.NONE);
		powerColumnTableColumn
				.setToolTipText("Total value (hours) of all units");
		powerColumnTableColumn.setWidth(59);
		powerColumnTableColumn.setText("power");

		final TableColumn playedColumnTableColumn = new TableColumn(
				playerTable, SWT.NONE);
		playedColumnTableColumn.setToolTipText("Number of Games Played");
		playedColumnTableColumn.setWidth(43);
		playedColumnTableColumn.setText("play");

		final TableColumn winPercColumnTableColumn = new TableColumn(
				playerTable, SWT.NONE);
		winPercColumnTableColumn.setToolTipText("Percentage of Games Won");
		winPercColumnTableColumn.setWidth(54);
		winPercColumnTableColumn.setText("win%");

		refreshButton = new Button(this, SWT.NONE);
		fdPlayerTable.bottom = new FormAttachment(refreshButton, -6);
		FormData fdRefreshButton = new FormData();
		fdRefreshButton.bottom = new FormAttachment(100, -80);
		fdRefreshButton.left = new FormAttachment(0);
		fdRefreshButton.height = 28;
		fdRefreshButton.width = 36;
		refreshButton.setLayoutData(fdRefreshButton);
		refreshButton.setToolTipText("Refresh player data");

		Group group = new Group(this, SWT.NONE);
		group.setLayout(new GridLayout(4, false));
		FormData fdGroup = new FormData();
		fdGroup.left = new FormAttachment(refreshButton, 6);
		fdGroup.right = new FormAttachment(100, 5);
		fdGroup.top = new FormAttachment(refreshButton, -25);
		group.setLayoutData(fdGroup);

		new Label(group, SWT.NONE);

		Label lblTheirRelation = new Label(group, SWT.NONE);
		lblTheirRelation.setText("Relation");

		Label lblNext = new Label(group, SWT.NONE);
		lblNext.setText("Next");

		Label lblSwitches = new Label(group, SWT.NONE);
		lblSwitches.setText("Switches");

		Label lblYou = new Label(group, SWT.NONE);
		lblYou.setText("You");

		yourRelationCombo = new Combo(group, SWT.NONE);
		yourRelationCombo.setSize(99, 25);
		yourRelationCombo.setVisibleItemCount(6);

		nextRelation = new Text(group, SWT.BORDER);
		nextRelation.setSize(97, 23);
		nextRelation.setEditable(false);

		switchesText = new Text(group, SWT.BORDER);
		switchesText.setSize(78, 23);
		switchesText.setEditable(false);

		Label lblThem = new Label(group, SWT.NONE);
		lblThem.setText("Them");

		theirRelationText = new Text(group, SWT.BORDER);
		theirRelationText.setSize(0, 23);
		theirRelationText.setEditable(false);

		theirNextRelationText = new Text(group, SWT.BORDER);
		theirNextRelationText.setSize(97, 23);
		theirNextRelationText.setEditable(false);

		theirSwitchesText = new Text(group, SWT.BORDER);
		theirSwitchesText.setSize(78, 23);
		theirSwitchesText.setEditable(false);

		Label lblPlayers = new Label(this, SWT.NONE);
		fdPlayerTable.top = new FormAttachment(lblPlayers, 5);
		FormData fdLblPlayers = new FormData();
		fdLblPlayers.top = new FormAttachment(teamTable, 5);
		fdLblPlayers.left = new FormAttachment(0, 5);
		lblPlayers.setLayoutData(fdLblPlayers);
		lblPlayers.setText("Players:");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public Table getPlayerTable() {
		return playerTable;
	}

	public Button getRefreshButton() {
		return refreshButton;
	}

	public Combo getMyRelationCombo() {
		return yourRelationCombo;
	}

	public Text getSwitchesText() {
		return switchesText;
	}

	public Text getTheirRelationText() {
		return theirRelationText;
	}

	public Text getTheirSwitchesText() {
		return theirSwitchesText;
	}

	public Text getTheirNextRelationText() {
		return theirNextRelationText;
	}

	public Text getNextRelation() {
		return nextRelation;
	}

	public Table getTeamTable() {
		return teamTable;
	}
}
