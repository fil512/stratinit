package com.kenstevens.stratinit.client.gwt.datasource;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kenstevens.stratinit.client.gwt.model.GWTGame;
import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.fields.DataSourceDateField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public abstract class GameDataSource extends StratInitDataSource {

	public GameDataSource(StatusSetter statusSetter) {
		super(statusSetter);
		setClientOnly(true);
		DataSourceIntegerField idField = new DataSourceIntegerField("id", "Id");
		idField.setPrimaryKey(true);
		setFields(
				idField,
				new DataSourceTextField("name", "Name"),
				new DataSourceIntegerField("size", "Size"),
				new DataSourceIntegerField("players", "Players"),
				new DataSourceDateField("created", "Created"),
				new DataSourceDateField("started", "Starts"),
				new DataSourceDateField("ends", "Ends"));
	}

	@Override
	protected void executeFetch(final String requestId, final DSRequest request,
			final DSResponse response) {

		AsyncCallback<List<GWTGame>> asyncCallback = new StratInitAsyncCallback<Integer, GWTGame>(this,
						requestId, response);
				fetchGames(asyncCallback);

	}

	protected abstract void fetchGames(AsyncCallback<List<GWTGame>> asyncCallback);
}
