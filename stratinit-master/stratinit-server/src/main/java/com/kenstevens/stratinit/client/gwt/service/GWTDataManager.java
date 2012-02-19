package com.kenstevens.stratinit.client.gwt.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.kenstevens.stratinit.client.gwt.event.GWTCitiesArrivedEvent;
import com.kenstevens.stratinit.client.gwt.event.GWTCoordsSelectedEvent;
import com.kenstevens.stratinit.client.gwt.event.GWTNationsArrivedEvent;
import com.kenstevens.stratinit.client.gwt.event.GWTSectorsArrivedEvent;
import com.kenstevens.stratinit.client.gwt.event.GWTSelectionSource;
import com.kenstevens.stratinit.client.gwt.event.GWTUnitsArrivedEvent;
import com.kenstevens.stratinit.client.gwt.event.GWTUnitsSelectedEvent;
import com.kenstevens.stratinit.client.gwt.model.GWTCity;
import com.kenstevens.stratinit.client.gwt.model.GWTData;
import com.kenstevens.stratinit.client.gwt.model.GWTHasHandlers;
import com.kenstevens.stratinit.client.gwt.model.GWTNation;
import com.kenstevens.stratinit.client.gwt.model.GWTSector;
import com.kenstevens.stratinit.client.gwt.model.GWTSectorCoords;
import com.kenstevens.stratinit.client.gwt.model.GWTUnit;
import com.kenstevens.stratinit.client.gwt.model.GWTUpdate;
import com.kenstevens.stratinit.client.gwt.model.GWTWorld;

public final class GWTDataManager extends GWTHasHandlers {
	private static final GWTDataManager instance = new GWTDataManager();
	private static GWTData data = null;

	private GWTDataManager() {}

	public static void setSelectedCoords(GWTSectorCoords selectedCoords, GWTSelectionSource source) {
		GWTCoordsSelectedEvent event = new GWTCoordsSelectedEvent(selectedCoords, source);
		instance.fireEvent(event);
		data.setSelectedCoords(selectedCoords);
	}

	public static GWTSectorCoords getSelectedCoords() {
		return data.getSelectedCoords();
	}

	public static <H extends EventHandler> HandlerRegistration addHandler(GwtEvent.Type<H> type, H handler) {
		return instance.addEventHandler(type, handler);
	}

	public static void init(int size) {
		data = new GWTData(size);
	}

	public static void setSectors(List<GWTSector> result) {
		data.setSectors(result);
		GWTSectorsArrivedEvent event = new GWTSectorsArrivedEvent(data.getSectors());
		instance.fireEvent(event);
	}

	public static void setNations(List<GWTNation> result) {
		GWTNationsArrivedEvent event = new GWTNationsArrivedEvent(data.getNationMap(), result);
		instance.fireEvent(event);
		data.setNations(result);
	}

	public static void setUnits(List<GWTUnit> result) {
		GWTUnitsArrivedEvent event = new GWTUnitsArrivedEvent(data.getUnitMap(), result);
		instance.fireEvent(event);
		data.setUnits(result);
	}

	public static void setCities(List<GWTCity> result) {
		GWTCitiesArrivedEvent event = new GWTCitiesArrivedEvent(data.getCityMap(), result);
		instance.fireEvent(event);
		data.setCities(result);
	}

	public static GWTSector[][] getSectors() {
		return data.getSectors();
	}

	public static GWTSector getSector(GWTSectorCoords coords) {
		return data.getSector(coords);
	}

	public static void setSelectedUnit(GWTUnit selectedUnit, GWTSelectionSource source) {
		List<GWTUnit> selectedUnits = new ArrayList<GWTUnit>();
		selectedUnits.add(selectedUnit);
		GWTUnitsSelectedEvent event = new GWTUnitsSelectedEvent(selectedUnits, source);
		instance.fireEvent(event);
		data.setSelectedUnit(selectedUnit);
	}

	public static List<GWTUnit> getSelectedUnits() {
		return data.getSelectedUnits();
	}

	public static void update(GWTUpdate update) {
		setNationId(update.nationId);
		setNations(update.nations);
		setSectors(update.sectors);
		setUnits(update.units);
		setCities(update.cities);
	}

	private static void setNationId(int nationId) {
		data.setNationId(nationId);
	}

	public static Map<Integer, GWTUnit> getUnitMap() {
		return data.getUnitMap();
	}

	public static Map<String, GWTCity> getCityMap() {
		return data.getCityMap();
	}

	public static void setSelectedUnit(int topUnitId, GWTSelectionSource source) {
		GWTUnit unit = data.getUnit(topUnitId);
		setSelectedUnit(unit, source);
	}

	public static void setSelectedUnits(List<Integer> unitIds,
			GWTSelectionSource source) {
		List<GWTUnit> selectedUnits = data.getSelectedUnits(unitIds);
		GWTUnitsSelectedEvent event = new GWTUnitsSelectedEvent(selectedUnits, source);
		instance.fireEvent(event);

		data.setSelectedUnits(selectedUnits);
	}

	public static GWTUnit getUnit(int id) {
		return data.getUnit(id);
	}

	public static GWTCity getCity(GWTSectorCoords coords) {
		return data.getCity(coords.toString());
	}

	public static GWTWorld getWorld() {
		return data.getWorld();
	}

	public static GWTNation getNation() {
		return data.getNation();
	}

	public static int getSize() {
		return data.getSize();
	}

	public static boolean initialized() {
		return data != null;
	}
}
