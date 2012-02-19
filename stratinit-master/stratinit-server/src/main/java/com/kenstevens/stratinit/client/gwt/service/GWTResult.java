package com.kenstevens.stratinit.client.gwt.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class GWTResult<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 997944575457386334L;
	private List<String> messages;
	public boolean success;
	private T value;

	public GWTResult() {}

	public GWTResult(String message, boolean success, T value) {
		setMessage(message);
		this.success = success;
		this.value = value;
	}

	public GWTResult(T value) {
		this("", true, value);
	}

	public GWTResult(boolean success) {
		this("", success, null);
	}

	public GWTResult(String message, boolean success) {
		this(message, success, null);
	}

	public GWTResult(List<String> messages, boolean success, T value) {
		this.messages = messages;
		this.success = success;
		this.value = value;
	}

	public GWTResult(List<String> messages, boolean success) {
		this(messages, success, null);
	}

	@Override
	public String toString() {
		return ""+success+" ["+getMessage()+"]";
	}
	private String getMessage() {
		String retval = "";
		for (String message : getMessages()) {
			retval += message+"\n";
		}
		return retval;
	}

	public final void setMessage(String message) {
		this.messages = new ArrayList<String>();
		this.getMessages().add(message);
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public List<String> getMessages() {
		return messages;
	}

	public String getLastMessage() {
		return messages.get(messages.size()-1);
	}
}
