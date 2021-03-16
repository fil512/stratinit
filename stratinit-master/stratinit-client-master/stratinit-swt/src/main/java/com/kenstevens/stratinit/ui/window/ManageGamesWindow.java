package com.kenstevens.stratinit.ui.window;

import com.kenstevens.stratinit.client.model.GameView;
import com.kenstevens.stratinit.shell.StratInitWindow;
import com.kenstevens.stratinit.ui.tabs.GameTable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.springframework.stereotype.Component;

@Component
public class ManageGamesWindow implements StratInitWindow {
	private Shell dialog;
	private Button joinButton;
	private GameTable joinGameTable;
	private Button selectGameButton;
	private GameTable myGamesTable;
	private TabFolder tabFolder;
	private TabItem myGamesTab;
	private TabItem joinGameTab;
	private Button noAlliancesButton;
	private Button alliancesAllowedButton;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open(Shell shell) {
		dialog = new Shell(shell);
		dialog.setMinimumSize(new Point(700, 400));
		dialog.setSize(912, 413);
		dialog.setLayout(null);
		shell.setText("Games");

		tabFolder = new TabFolder(dialog, SWT.NONE);
		tabFolder.setBounds(10, 10, 884, 347);

		myGamesTab = new TabItem(tabFolder, SWT.NONE);
		myGamesTab.setText("My Games");

		Group group = new Group(tabFolder, SWT.NONE);
		myGamesTab.setControl(group);

		myGamesTable = new GameTable(group, SWT.NONE);
		myGamesTable.setBounds(0, 0, 864, 271);

		selectGameButton = new Button(group, SWT.NONE);
		selectGameButton.setBounds(0, 277, 84, 27);
		selectGameButton.setText("Enter");

		joinGameTab = new TabItem(tabFolder, SWT.NONE);
		joinGameTab.setText("Join Game");

		Group joinGroup = new Group(tabFolder, SWT.NONE);
		joinGameTab.setControl(joinGroup);
		joinGroup.setLayout(new FormLayout());

		joinGameTable = new GameTable(joinGroup, SWT.NONE);
		FormData fdJoinGameTable = new FormData();
		fdJoinGameTable.bottom = new FormAttachment(0, 265);
		fdJoinGameTable.top = new FormAttachment(0, -7);
		fdJoinGameTable.left = new FormAttachment(0, -3);
		joinGameTable.setLayoutData(fdJoinGameTable);

		joinButton = new Button(joinGroup, SWT.NONE);
		FormData fdJoinButton = new FormData();
		fdJoinButton.right = new FormAttachment(0, 46);
		fdJoinButton.top = new FormAttachment(0, 268);
		fdJoinButton.left = new FormAttachment(0, -3);
		joinButton.setLayoutData(fdJoinButton);
		joinButton.setText("Join");
		
		Label label = new Label(dialog, SWT.NONE);
		label.setBounds(10, 364, 122, 15);
		label.setText("Game Type Preference:");
		
		alliancesAllowedButton = new Button(dialog, SWT.RADIO);
		alliancesAllowedButton.setBounds(138, 363, 114, 16);
		alliancesAllowedButton.setText("Alliances Allowed");
		alliancesAllowedButton.setSelection(true);
		
		noAlliancesButton = new Button(dialog, SWT.RADIO);
		noAlliancesButton.setBounds(258, 363, 87, 16);
		noAlliancesButton.setText("No Alliances");
		dialog.open();
	}

	public Button getSelectGameButton() {
		return selectGameButton;
	}

	public void close() {
		dialog.close();
	}

	public Button getJoinButton() {
		return joinButton;
	}

	public GameTable getJoinGameTable() {
		return joinGameTable;
	}

	public GameTable getMyGamesTable() {
		return myGamesTable;
	}

	public TabFolder getTabFolder() {
		return tabFolder;
	}

	public Shell getShell() {
		return dialog;
	}

	public void selectGamesTab() {
		tabFolder.setSelection(myGamesTab);
	}
	
	public void selectJoinGameTab() {
		tabFolder.setSelection(joinGameTab);
	}
	
	public boolean isNoAlliancesAllowedSelected() {
		return noAlliancesButton.getSelection();
	}
	
	public void setNoAlliancesButton(GameView game) {
		if (game == null) {
			alliancesAllowedButton.setEnabled(true);
			noAlliancesButton.setEnabled(true);
			alliancesAllowedButton.setSelection(true);
			noAlliancesButton.setSelection(false);
			return;
		}
		if (game.hasStarted() || !game.isMapped()) {
			alliancesAllowedButton.setEnabled(false);
			noAlliancesButton.setEnabled(false);
		} else {
			alliancesAllowedButton.setEnabled(true);
			noAlliancesButton.setEnabled(true);
		}
		alliancesAllowedButton.setSelection(!game.isMyNoAlliances());
		noAlliancesButton.setSelection(game.isMyNoAlliances());
	}
}
