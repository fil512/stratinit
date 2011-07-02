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

import com.kenstevens.stratinit.ui.shell.Window;
import com.kenstevens.stratinit.ui.tabs.GameTable;

@Component
public class ManageGamesWindow implements Window {
	private Shell dialog;
	private Button joinButton;
	private GameTable joinGameTable;
	private Button selectGameButton;
	private GameTable myGamesTable;
	private TabFolder tabFolder;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open(Shell shell) {
		dialog = new Shell(shell);
		dialog.setMinimumSize(new Point(700, 400));
		dialog.setSize(845, 523);
		dialog.setLayout(null);
		shell.setText("Games");

		tabFolder = new TabFolder(dialog, SWT.NONE);
		tabFolder.setBounds(10, 10, 674, 342);

		TabItem tbtmMyGames = new TabItem(tabFolder, SWT.NONE);
		tbtmMyGames.setText("My Games");

		Group group = new Group(tabFolder, SWT.NONE);
		tbtmMyGames.setControl(group);

		myGamesTable = new GameTable(group, SWT.NONE);
		myGamesTable.setBounds(0, 0, 614, 271);

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
}
