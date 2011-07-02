package com.kenstevens.stratinit.client.gwt.widget.play.tab;

import com.kenstevens.stratinit.client.gwt.event.GWTSelectionSource;
import com.kenstevens.stratinit.client.gwt.widget.UnitGridPanel;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VStack;

public class UnitTab extends CanvasTab {
	private UnitGridPanel unitGrid;

	public UnitTab() {
		Label unitLabel = new Label();
		unitLabel.setContents("Units: ");

		unitGrid = new UnitGridPanel(GWTSelectionSource.UNIT_TAB, SelectionStyle.SINGLE);

		VStack column = new VStack();
		column.setWidth100();
		column.setHeight100();
		column.addMember(unitLabel);
		VStack unitGridColumn = new VStack();
		unitGridColumn.setHeight100();
		unitGridColumn.setOverflow(Overflow.AUTO);
		unitGridColumn.addMember(unitGrid);
		column.addMember(unitGridColumn);
		this.addChild(column);
	}

	public void refreshData() {
		unitGrid.refreshData();
	}
}
