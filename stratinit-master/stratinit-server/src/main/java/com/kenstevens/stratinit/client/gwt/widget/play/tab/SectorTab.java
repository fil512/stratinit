package com.kenstevens.stratinit.client.gwt.widget.play.tab;

import com.kenstevens.stratinit.client.gwt.event.GWTCoordsSelectedEventHandler;
import com.kenstevens.stratinit.client.gwt.event.GWTSelectionSource;
import com.kenstevens.stratinit.client.gwt.model.GWTSectorCoords;
import com.kenstevens.stratinit.client.gwt.service.GWTDataManager;
import com.kenstevens.stratinit.client.gwt.widget.UnitGridPanel;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VStack;

public class SectorTab extends CanvasTab {


	private Label sectorField;
	private UnitGridPanel unitGrid;

	public SectorTab() {

		HLayout sectorLayout = new HLayout();
		Label sectorLabel = new Label();
		sectorLabel.setContents("Sector: ");
		sectorField = new Label();
		sectorField.setWidth100();
		sectorField.setContents("0,0");
		sectorLayout.addMember(sectorLabel);
		sectorLayout.addMember(sectorField);
		BuildPanel buildPanel = new BuildPanel();
		BuildPanelControl.addHandlers(buildPanel);
		unitGrid = new UnitGridPanel(GWTSelectionSource.MAP, SelectionStyle.MULTIPLE);

		VStack column = new VStack();
		column.setWidth100();
		column.setHeight100();
		column.addMember(sectorLayout);
		VStack unitGridColumn = new VStack();
		unitGridColumn.setHeight100();
		unitGridColumn.setOverflow(Overflow.AUTO);
		unitGridColumn.addMember(unitGrid);
		column.addMember(buildPanel);
		column.addMember(unitGridColumn);

		this.addChild(column);

		addHandlers();
	}

	private void addHandlers() {
		GWTCoordsSelectedEventHandler handler = new GWTCoordsSelectedEventHandler() {

			@Override
			public void coordsSelected(GWTSectorCoords selectedCoords, GWTSelectionSource source) {
				selectCoords(selectedCoords);
			}

		};
		GWTDataManager.addHandler(GWTCoordsSelectedEventHandler.TYPE, handler);
	}

	private void selectCoords(
			GWTSectorCoords selectedCoords) {
		sectorField.setContents(selectedCoords.toString());
		unitGrid.filter(selectedCoords);
	}
}
