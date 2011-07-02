package com.kenstevens.stratinit.client.gwt.datasource;

import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;

public abstract class StratInitDataSource extends GwtRpcDataSource {
	private final StatusSetter statusSetter;

	public StratInitDataSource(StatusSetter statusSetter) {
		this.statusSetter = statusSetter;
		setClientOnly(true);
	}

	@Override
	protected void executeAdd(String requestId, DSRequest request,
			DSResponse response) {
	}

	@Override
	protected void executeRemove(String requestId, DSRequest request,
			DSResponse response) {
	}

	@Override
	protected void executeUpdate(String requestId, DSRequest request,
			DSResponse response) {
	}

	public StatusSetter getStatusSetter() {
		return statusSetter;
	}
}
