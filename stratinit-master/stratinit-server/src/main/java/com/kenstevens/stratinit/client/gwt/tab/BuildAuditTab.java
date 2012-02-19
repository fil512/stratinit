package com.kenstevens.stratinit.client.gwt.tab;

import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
import com.kenstevens.stratinit.client.gwt.widget.BuildAuditGridPanel;
import com.smartgwt.client.widgets.Canvas;

public class BuildAuditTab extends Canvas {
	public BuildAuditTab(String title, StatusSetter statusSetter) {
		this.setWidth(470);
		this.setHeight(400);
		BuildAuditGridPanel playerGrid = new BuildAuditGridPanel(statusSetter, title);
		this.addChild(playerGrid);
	}
}
