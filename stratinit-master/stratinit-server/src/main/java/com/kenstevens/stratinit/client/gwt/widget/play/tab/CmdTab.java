package com.kenstevens.stratinit.client.gwt.widget.play.tab;

import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VStack;

public class CmdTab extends CanvasTab {
	private HTMLFlow htmlFlow;

	public CmdTab() {
		Label titleLabel = new Label();
		titleLabel.setContents("Commands: ");

		htmlFlow = new HTMLFlow();

		VStack column = new VStack();
		column.setWidth100();
		column.setHeight100();
		column.addMember(titleLabel);
		VStack cmdColumn = new VStack();
		cmdColumn.setHeight100();
		cmdColumn.setOverflow(Overflow.AUTO);
		cmdColumn.addMember(htmlFlow);
		column.addMember(cmdColumn);
		this.addChild(column);
	}

	public void addText(String message) {
		htmlFlow.setContents(htmlFlow.getContents()+"<br/>\n"+message);
	}
}
