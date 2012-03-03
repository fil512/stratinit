package com.kenstevens.stratinit.ui.shell;

public class Message {
	private final String text;
	private final Type type;
	public enum Type {ACTION, RESPONSE, ERROR, LOGIN_ERROR};

	public Message(String text, Type type) {
		this.text = text;
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public Type getType() {
		return type;
	}

	public boolean isError() {
		return type == Type.ERROR || type == Type.LOGIN_ERROR;
	}

	public String toString() {
		return type+": "+text;
	}
}
