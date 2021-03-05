package com.kenstevens.stratinit.client.control;

import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.client.control.selection.SelectEvent;
import com.kenstevens.stratinit.client.control.selection.SelectSectorEvent;
import com.kenstevens.stratinit.client.control.selection.SelectUnitsEvent;
import com.kenstevens.stratinit.client.control.selection.Selection.Source;
import com.kenstevens.stratinit.client.event.CityListArrivedEvent;
import com.kenstevens.stratinit.client.event.CityListReplacementArrivedEvent;
import com.kenstevens.stratinit.client.event.StratinitEventBus;
import com.kenstevens.stratinit.client.model.CityView;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.SelectedCity;
import com.kenstevens.stratinit.move.WorldView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

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
	protected StratinitEventBus eventBus;
	
	private CityTableItemControl cityTableItemControl;

	public CityTableControl(Table table) {
		this.table = table;
		setTableListeners();
	}

	@Subscribe
	public void handleCityListReplacementArrivedEvent(CityListReplacementArrivedEvent event) {
		cityTableItemControl.clear();
		cityTableItemControl.tablifyCities();
		table.redraw();
	}
	
	@Subscribe
	public void handleCityListArrivedEvent(CityListArrivedEvent event) {
		updateTable();
	}
	
	@Subscribe
	public void handleSelectSectorEvent(SelectSectorEvent event) {
		selectCity();
	}
	
	@Subscribe
	public void handleSelectUnitsEvent(SelectUnitsEvent event) {
		selectCity();
	}
	
	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		cityTableItemControl = new CityTableItemControl(table, db);
		eventBus.register(this);
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
