package com.kenstevens.stratinit.client.gwt.model;

import com.kenstevens.stratinit.client.gwt.status.StatusSetter;

public final class MainStatusReporter {
	private static StatusSetter statusSetter;
	
	private MainStatusReporter() {}

	public static void setStatusSetter(StatusSetter statusSetter) {
		MainStatusReporter.statusSetter = statusSetter;
	}

	public static void addText(String message) {
		statusSetter.addText(message);
	}

	public static StatusSetter getStatusSetter() {
		return statusSetter;
	}
}
