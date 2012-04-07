package com.kenstevens.stratinit.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.kenstevens.stratinit.shell.TopShell;

public final class PlayerCommandPointWarner {
	
	public PlayerCommandPointWarner() {}

	public static boolean beenWarned = false;

	public static boolean warnUserLowCommandPoints(TopShell topShell,
			int commandPoints) {
		if (beenWarned) {
			return true;
		}
		beenWarned = true;

		MessageBox messageBox = new MessageBox(topShell.getShell(), SWT.OK
				| SWT.CANCEL | SWT.ICON_WARNING);
		messageBox.setMessage("WARNING: You are down to your last "
				+ commandPoints + " command points!");
		int button = messageBox.open();
		return button == SWT.OK;
	}
}
