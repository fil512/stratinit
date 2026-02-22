package com.kenstevens.stratinit.remote.exception;

import java.util.List;

public class CommandFailedException extends StratInitException {
	private final List<String> messages;

	public CommandFailedException(List<String> messages) {
		super(String.join("\n", messages));
		this.messages = messages;
	}

	public List<String> getMessages() {
		return messages;
	}
}
