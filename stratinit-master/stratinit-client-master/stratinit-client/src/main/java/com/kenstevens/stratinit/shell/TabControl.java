package com.kenstevens.stratinit.shell;

public interface TabControl {

	boolean battleTabSelected();

	void switchToSectorTab();

	boolean playerTabSelected();

	boolean cityTabSelected();

	boolean supplyTabSelected();

	boolean unitTabSelected();
}
