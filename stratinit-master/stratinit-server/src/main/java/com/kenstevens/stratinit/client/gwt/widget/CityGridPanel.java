package com.kenstevens.stratinit.client.gwt.widget;

import java.util.List;
import java.util.Map;

import com.kenstevens.stratinit.client.gwt.datasource.CityDataSource;
import com.kenstevens.stratinit.client.gwt.event.GWTCitiesArrivedEventHandler;
import com.kenstevens.stratinit.client.gwt.event.GWTCoordsSelectedEventHandler;
import com.kenstevens.stratinit.client.gwt.event.GWTSelectionSource;
import com.kenstevens.stratinit.client.gwt.model.CityListGridRecord;
import com.kenstevens.stratinit.client.gwt.model.GWTCity;
import com.kenstevens.stratinit.client.gwt.model.GWTSectorCoords;
import com.kenstevens.stratinit.client.gwt.service.GWTDataManager;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;

public class CityGridPanel extends ListGrid {
	private CityDataSource dataSource;
	private final GridSynchronizer<String, GWTCity, CityListGridRecord>gridSynchronizer;

	public CityGridPanel(final GWTSelectionSource source, SelectionStyle selectionStyle) {
		addRecordClickHandler(new RecordClickHandler() {
			@Override
			public void onRecordClick(RecordClickEvent event) {
				GWTDataManager.setSelectedCoords(getSelectedCoords(), source);
			}
		});
        setWidth100();
        setHeight100();
        setSelectionType(selectionStyle);
        dataSource = new CityDataSource();
		setDataSource(dataSource);
		gridSynchronizer = new GridSynchronizer<String, GWTCity, CityListGridRecord>(this);
		addHandlers();
	}

	private void addHandlers() {
		GWTCoordsSelectedEventHandler chandler = new GWTCoordsSelectedEventHandler() {

			@Override
			public void coordsSelected(GWTSectorCoords selectedCoords, GWTSelectionSource source) {
				GWTCity city = GWTDataManager.getCityMap().get(selectedCoords.toString());
				if (city == null) {
					return;
				}
				for (Record record : getRecords()) {
					CityListGridRecord cityRecord = (CityListGridRecord)record;
					if (cityRecord.getCity().coords.equals(selectedCoords)) {
						selectRecord(record);
						break;
					}
				}
			}
		};
		GWTDataManager.addHandler(GWTCoordsSelectedEventHandler.TYPE, chandler);

		GWTCitiesArrivedEventHandler cahandler = new GWTCitiesArrivedEventHandler() {
			@Override
			public void receiveNewCities(Map<String, GWTCity> cityMap, List<GWTCity> result) {
				gridSynchronizer.sync(result);
			}
		};
		GWTDataManager.addHandler(GWTCitiesArrivedEventHandler.TYPE, cahandler);
	}

	protected GWTSectorCoords getSelectedCoords() {
		CityListGridRecord record = (CityListGridRecord) getSelection()[0];
		return record.getCity().coords;
	}

	public void refreshData() {
		setData(new CityListGridRecord[0]);
		super.fetchData();
	}
}
