package com.kenstevens.stratinit.client.gwt.widget.play.tab;

import com.kenstevens.stratinit.client.gwt.event.GWTSelectionSource;
import com.kenstevens.stratinit.client.gwt.widget.CityGridPanel;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VStack;

public class CityTab extends CanvasTab {
	private CityGridPanel cityGrid;

	public CityTab() {
		Label unitLabel = new Label();
		unitLabel.setContents("Cities: ");

		cityGrid = new CityGridPanel(GWTSelectionSource.UNIT_TAB, SelectionStyle.SINGLE);

		VStack column = new VStack();
		column.setWidth100();
		column.setHeight100();
		column.addMember(unitLabel);
		VStack cityGridColumn = new VStack();
		cityGridColumn.setHeight100();
		cityGridColumn.setOverflow(Overflow.AUTO);
		cityGridColumn.addMember(cityGrid);
		column.addMember(cityGridColumn);
		this.addChild(column);
	}

	public void refreshData() {
		cityGrid.refreshData();
	}
}
