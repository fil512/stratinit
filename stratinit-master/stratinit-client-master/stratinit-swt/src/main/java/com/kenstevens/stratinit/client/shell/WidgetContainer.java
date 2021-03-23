package com.kenstevens.stratinit.client.shell;

import org.springframework.stereotype.Service;


@Service
public class WidgetContainer {
	private ProgressBarControl progressBarControl;
	private CommandListControl commandListControl;
	private MapControl mapControl;
	private TabControl tabControl;
	private GameManager gameManager;

	public void setProgressBarControl(ProgressBarControl progressBarControl) {
		this.progressBarControl = progressBarControl;
	}

	public ProgressBarControl getProgressBarControl() {
		return progressBarControl;
	}

	public void setCommandListControl(CommandListControl commandListControl) {
		this.commandListControl = commandListControl;
	}

	public CommandListControl getCommandListControl() {
		return commandListControl;
	}

	public void setMapControl(MapControl mapControl) {
		this.mapControl = mapControl;
	}

	public MapControl getMapControl() {
		return mapControl;
	}

	public void setTabControl(TabControl tabControl) {
		this.tabControl = tabControl;
	}

	public TabControl getTabControl() {
		return tabControl;
	}

	public void setGameManager(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public GameManager getGameManager() {
		return gameManager;
	}
}
