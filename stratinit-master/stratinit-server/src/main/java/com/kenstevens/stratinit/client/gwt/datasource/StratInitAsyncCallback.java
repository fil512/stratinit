package com.kenstevens.stratinit.client.gwt.datasource;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kenstevens.stratinit.client.gwt.model.GWTEntity;
import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class StratInitAsyncCallback<I, T extends GWTEntity<I>> implements AsyncCallback<List<T>> {
	private final StatusSetter statusSetter;
	private StratInitDataSource stratInitDataSource;
	private String requestId;
	private DSResponse response;

	public StratInitAsyncCallback(StratInitDataSource stratInitDataSource, String requestId,
			DSResponse response) {
		this.statusSetter = stratInitDataSource.getStatusSetter();
		this.stratInitDataSource = stratInitDataSource;
		this.requestId = requestId;
		this.response = response;
	}

	@Override
	public void onFailure(Throwable e) {
		if (statusSetter != null) {
			statusSetter.setText(e.getMessage());
		}
		if (response != null) {
			response.setStatus(RPCResponse.STATUS_FAILURE);
		}
		if (stratInitDataSource != null) {
			stratInitDataSource.processResponse(requestId, response);
		}
	}

	/**
	 * Create ListGridRecord from the List<State> to return
	 *
	 * @param result
	 */
	@Override
	public void onSuccess(List<T> result) {
		ListGridRecord[] records = new ListGridRecord[result.size()];
		for (int x=0; x<result.size(); x++) {
			records[x] = result.get(x).getListGridRecord();
		}
		response.setData(records);
		response.setTotalRows (result.size());	// for paging to work we have to specify size of full result set
		stratInitDataSource.processResponse(requestId, response);
	}
}
