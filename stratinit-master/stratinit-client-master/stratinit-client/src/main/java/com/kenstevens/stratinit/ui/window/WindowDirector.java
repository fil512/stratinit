package com.kenstevens.stratinit.ui.window;


public interface WindowDirector {

	public abstract void accountSettingsWindow();

	public abstract void openNewsWindow();

	public abstract void openStatsWindow(final int gameId);

	public abstract void chooseGameWindow();
}