package com.kenstevens.stratinit.client.gwt.datasource;

import com.kenstevens.stratinit.client.gwt.model.GWTPlayer;
import com.kenstevens.stratinit.client.gwt.service.GWTPlayerService;
import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class PlayerDataSource extends StratInitDataSource {

	public PlayerDataSource(StatusSetter statusSetter) {
		super(statusSetter);
		setClientOnly(true);
		DataSourceTextField nameField = new DataSourceTextField("name", "Name");
		nameField.setPrimaryKey(true);
		setFields(nameField,
				new DataSourceTextField("wins", "Won"),
				new DataSourceIntegerField("wins", "Won"),
				new DataSourceIntegerField("played", "Played"),
				new DataSourceTextField("winperc", "Win %"),
				new DataSourceBooleanField("enabled", "Enabled"),
				new DataSourceTextField("email", "Emails"),
				new DataSourceTextField("emailGameMail", "Game Mail?")
		);

	}

	@Override
	protected void executeFetch(final String requestId, final DSRequest request,
			final DSResponse response) {
		GWTPlayerService.Util.getInstance().fetch(
				new StratInitAsyncCallback<String, GWTPlayer>(this,
						requestId, response));
	}
}
