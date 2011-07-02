package com.kenstevens.stratinit.client.gwt.widget;

import com.kenstevens.stratinit.client.gwt.datasource.BuildAuditDataSource;
import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
import com.smartgwt.client.widgets.grid.ListGrid;

public class BuildAuditGridPanel extends ListGrid {

	public BuildAuditGridPanel(StatusSetter statusSetter, final String title) {
		setWidth(730);
		setHeight(400);
		setTitle(title);
		BuildAuditDataSource buildAuditDataSource = new BuildAuditDataSource(statusSetter);
		setDataSource(buildAuditDataSource);
		setAutoFetchData(true);
		setCanDragSelectText(true);
	}
}
