package com.kenstevens.stratinit.client.gwt.model;

import com.kenstevens.stratinit.client.gwt.status.StatusSetter;

public final class StatusReporter {
	private static StatusSetter statusSetter;
	
	private StatusReporter() {}

	public static void setStatusSetter(StatusSetter statusSetter) {
		StatusReporter.statusSetter = statusSetter;
	}

	public static void addText(String message) {
		statusSetter.addText(message);
	}

	public static StatusSetter getStatusSetter() {
		return statusSetter;
	}
}
