package com.kenstevens.stratinit.client.gwt.service;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
public class StatusSetterAsyncCallback<T extends Serializable> implements
		AsyncCallback<GWTResult<T>> {

	private final StatusSetter statusSetter;

	public StatusSetterAsyncCallback(StatusSetter statusSetter) {

		this.statusSetter = statusSetter;
	}

	public void onFailure(Throwable caught) {
		statusSetter.setText(caught.getMessage());
	}

	public void onSuccess(GWTResult<T> result) {
		if (result.success) {
			statusSetter.setText(result.getLastMessage());
		} else {
			statusSetter.setText("ERROR: " + result.getLastMessage());
		}

	}
}
