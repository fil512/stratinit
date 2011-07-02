package com.kenstevens.stratinit.client.gwt.datasource;

import com.kenstevens.stratinit.client.gwt.model.GWTBuildAudit;
import com.kenstevens.stratinit.client.gwt.service.GWTGameService;
import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class BuildAuditDataSource extends StratInitDataSource {

	public BuildAuditDataSource(StatusSetter statusSetter) {
		super(statusSetter);
		setClientOnly(true);
		setFields(new DataSourceIntegerField("game", "Game"),
				new DataSourceTextField("type", "Unit Type"),
				new DataSourceIntegerField("count", "Count"));
	}

	@Override
	protected void executeFetch(final String requestId, final DSRequest request,
			final DSResponse response) {
		GWTGameService.Util.getInstance().fetchBuildAudit(
				new StratInitAsyncCallback<Integer, GWTBuildAudit>(this,
						requestId, response));
	}

}
