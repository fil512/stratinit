package com.kenstevens.stratinit.control;

import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gwt.event.shared.HandlerManager;
import com.kenstevens.stratinit.control.selection.SelectEvent;
import com.kenstevens.stratinit.control.selection.SelectSectorEvent;
import com.kenstevens.stratinit.control.selection.SelectSectorEventHandler;
import com.kenstevens.stratinit.control.selection.SelectUnitsEvent;
import com.kenstevens.stratinit.control.selection.SelectUnitsEventHandler;
import com.kenstevens.stratinit.control.selection.Selection.Source;
import com.kenstevens.stratinit.event.CityListArrivedEvent;
import com.kenstevens.stratinit.event.CityListArrivedEventHandler;
import com.kenstevens.stratinit.event.CityListReplacementArrivedEvent;
import com.kenstevens.stratinit.event.CityListReplacementArrivedEventHandler;
import com.kenstevens.stratinit.model.CityView;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.SelectedCity;
import com.kenstevens.stratinit.move.WorldView;

@Scope("prototype")
@Component
public class CityTableControl implements Controller {
	private final Table table;
	@Autowired
	private Data db;
	@Autowired
	private SelectEvent selectEvent;
	@Autowired
	private SelectedCity selectedCity;
	@Autowired
	private HandlerManager handlerManager;
	
	private CityTableItemControl cityTableItemControl;

	public CityTableControl(Table table) {
		this.table = table;
		setTableListeners();
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		cityTableItemControl = new CityTableItemControl(table, db);
		handlerManager.addHandler(CityListArrivedEvent.TYPE,
				new CityListArrivedEventHandler() {
					@Override
					public void dataArrived() {
						updateTable();
					}
				});
		
		handlerManager.addHandler(CityListReplacementArrivedEvent.TYPE,
				new CityListReplacementArrivedEventHandler() {
					@Override
					public void dataArrived() {
						cityTableItemControl.clear();
						cityTableItemControl.tablifyCities();
						table.redraw();
					}
				});
		handlerManager.addHandler(CityListReplacementArrivedEvent.TYPE,
				new CityListReplacementArrivedEventHandler() {
					@Override
					public void dataArrived() {
						cityTableItemControl.clear();
						cityTableItemControl.tablifyCities();
						table.redraw();
					}
				});
		handlerManager.addHandler(SelectSectorEvent.TYPE,
				new SelectSectorEventHandler() {
					@Override
					public void selectSector(Source selectionSource) {
						selectCity();
					}
				});
		handlerManager.addHandler(SelectUnitsEvent.TYPE,
				new SelectUnitsEventHandler() {
					@Override
					public void selectUnits(Source selectionSource) {
						selectCity();
					}
				});
	}

	public final void setTableListeners() {
		table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				TableItem item = (TableItem) event.item;
				if (item == null) {
					return;
				}
				CityView city = (CityView) item.getData();
				if (city == null) {
					return;
				}
				selectEvent.selectSectorCoords(city.getCoords(), Source.CITY_TAB);
			}
		});
	}

	private void updateTable() {
		List<CityView> dbCities = db.getCityList().getCities();
		WorldView worldView = db.getWorld();
		for (TableItem item : table.getItems()) {
			updateTableItem(worldView, dbCities, item);
		}
	}

	private void updateTableItem(WorldView worldView, List<CityView> dbCities,
			TableItem item) {
		CityView listCity = (CityView) item.getData();
		if (dbCities.contains(listCity)) {
			item.setText(cityTableItemControl
					.toStringArray(worldView, listCity));
		} else {
			// City has been lost
			item.setText(cityTableItemControl.deadCityStringArray());
		}
	}

	private void selectCity() {
		CityView city = selectedCity.getCity();
		if (city == null) {
			return;
		}
		if (!cityTableItemControl.contains(city)) {
			return;
		}

		int index = 0;
		for (TableItem tableItem : table.getItems()) {
			if (tableItem.getData() == city) {
				table.setSelection(index);
				break;
			}
			++index;
		}
	}
}
