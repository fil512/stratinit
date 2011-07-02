package com.kenstevens.stratinit.client.gwt.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kenstevens.stratinit.client.gwt.datasource.UnitDataSource;
import com.kenstevens.stratinit.client.gwt.event.GWTSelectionSource;
import com.kenstevens.stratinit.client.gwt.event.GWTUnitsArrivedEventHandler;
import com.kenstevens.stratinit.client.gwt.event.GWTUnitsSelectedEventHandler;
import com.kenstevens.stratinit.client.gwt.model.GWTSectorCoords;
import com.kenstevens.stratinit.client.gwt.model.GWTUnit;
import com.kenstevens.stratinit.client.gwt.model.StatusReporter;
import com.kenstevens.stratinit.client.gwt.model.UnitListGridRecord;
import com.kenstevens.stratinit.client.gwt.service.GWTDataManager;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;

public class UnitGridPanel extends ListGrid {
	private UnitDataSource dataSource;
	private GridSynchronizer<Integer, GWTUnit, UnitListGridRecord>gridSynchronizer;

	public UnitGridPanel(final GWTSelectionSource source, SelectionStyle selectionStyle) {
		addRecordClickHandler(new RecordClickHandler() {
			@Override
			public void onRecordClick(RecordClickEvent event) {
				GWTDataManager.setSelectedUnits(getSelectedUnits(), source);
			}
		});
        setWidth100();
        setHeight100();
        setSelectionType(selectionStyle);
        dataSource = new UnitDataSource();
		setDataSource(dataSource);
		gridSynchronizer = new GridSynchronizer<Integer, GWTUnit, UnitListGridRecord>(this);
		this.setShowFilterEditor(true);
		addHandlers();
	}

	private void addHandlers() {
		GWTUnitsSelectedEventHandler uhandler = new GWTUnitsSelectedEventHandler() {
			@Override
			public void unitsSelected(List<GWTUnit> selectedUnits, GWTSelectionSource source) {
				if (selectedUnits.isEmpty()) {
					return;
				}
				for (Record record : getRecords()) {
					UnitListGridRecord unitRecord = (UnitListGridRecord)record;
					if (selectedUnits.contains(unitRecord.getUnit())) {
						StatusReporter.addText("selecting "+unitRecord.getUnit().unitType+" at "+unitRecord.getUnit().coords);
						selectRecord(record, false);
					}
				}
			}
		};
		GWTDataManager.addHandler(GWTUnitsSelectedEventHandler.TYPE, uhandler);

		GWTUnitsArrivedEventHandler uahandler = new GWTUnitsArrivedEventHandler() {
			@Override
			public void receiveNewUnits(Map<Integer, GWTUnit> unitMap, List<GWTUnit> result) {
				gridSynchronizer.sync(result);
			}
		};
		GWTDataManager.addHandler(GWTUnitsArrivedEventHandler.TYPE, uahandler);
	}

	protected GWTUnit getSelectedUnit() {
		UnitListGridRecord record = (UnitListGridRecord) getSelection()[0];
		return record.getUnit();
	}

	public void refreshData() {
		setData(new UnitListGridRecord[0]);
		super.fetchData();
	}

	public List<Integer> getSelectedUnits() {
		List<Integer> retval = new ArrayList<Integer>();
		for (ListGridRecord record : getSelection()) {
			retval.add(record.getAttributeAsInt("id"));
		}
		return retval;
	}

	public void filter(GWTSectorCoords selectedCoords) {
		Criteria criteria = new Criteria();
		criteria.addCriteria("coords", selectedCoords.toString());
		this.setFilterEditorCriteria(criteria);
		this.filterByEditor();
	}
}
