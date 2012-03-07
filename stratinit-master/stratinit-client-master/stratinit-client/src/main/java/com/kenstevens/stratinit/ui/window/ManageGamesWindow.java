package com.kenstevens.stratinit.ui.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.ui.shell.StratInitWindow;
import com.kenstevens.stratinit.ui.tabs.GameTable;
import org.eclipse.swt.widgets.Label;

@Component
public class ManageGamesWindow implements StratInitWindow {
	private Shell dialog;
	private Button joinButton;
	private GameTable joinGameTable;
	private Button selectGameButton;
	private GameTable myGamesTable;
	private TabFolder tabFolder;
	private TabItem myGamesTab;
	private Button noAlliancesButton;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open(Shell shell) {
		dialog = new Shell(shell);
		dialog.setMinimumSize(new Point(700, 400));
		dialog.setSize(912, 405);
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

		TabItem tbtmJoinGame = new TabItem(tabFolder, SWT.NONE);
		tbtmJoinGame.setText("Join Game");

		Group joinGroup = new Group(tabFolder, SWT.NONE);
		tbtmJoinGame.setControl(joinGroup);
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
		
		Label label = new Label(joinGroup, SWT.NONE);
		label.setText("Game Type Preference:");
		FormData fdLabel = new FormData();
		fdLabel.top = new FormAttachment(joinButton, 5, SWT.TOP);
		label.setLayoutData(fdLabel);
		
		Button alliancesAllowedButton = new Button(joinGroup, SWT.RADIO);
		fdLabel.right = new FormAttachment(alliancesAllowedButton, -13);
		alliancesAllowedButton.setText("Alliances Allowed");
		alliancesAllowedButton.setSelection(true);
		FormData fdButton = new FormData();
		fdButton.left = new FormAttachment(0, 208);
		fdButton.top = new FormAttachment(joinButton, 4, SWT.TOP);
		alliancesAllowedButton.setLayoutData(fdButton);
		
		noAlliancesButton = new Button(joinGroup, SWT.RADIO);
		noAlliancesButton.setText("No Alliances");
		FormData fdButton1 = new FormData();
		fdButton1.top = new FormAttachment(joinButton, 4, SWT.TOP);
		fdButton1.left = new FormAttachment(alliancesAllowedButton, 6);
		noAlliancesButton.setLayoutData(fdButton1);
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
	
	public boolean isNoAlliancesAllowedSelected() {
		return noAlliancesButton.getSelection();
	}
}
