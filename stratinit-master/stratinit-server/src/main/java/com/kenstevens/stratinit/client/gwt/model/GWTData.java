package com.kenstevens.stratinit.client.gwt.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GWTData {
	private GWTSectorCoords selectedCoords = new GWTSectorCoords();
	private final List<GWTUnit> selectedUnits = new ArrayList<GWTUnit>();
	private GWTWorld world;
	private final Map<Integer, GWTUnit> unitMap = new HashMap<Integer, GWTUnit>();
	private final Map<Integer, GWTNation> nationMap = new HashMap<Integer, GWTNation>();
	private final Map<String, GWTCity> cityMap = new HashMap<String, GWTCity>();
	private int nationId;
	private int size;
	
	public GWTData(int size) {
		this.size = size;
		world = new GWTWorld(size);
	}

	public void setUnits(List<GWTUnit> result) {
		unitMap.clear();
		for (GWTUnit unit : result) {
			unitMap.put(unit.id, unit);
		}
	}

	public void setNations(List<GWTNation> result) {
		nationMap.clear();
		for (GWTNation nation : result) {
			nationMap.put(nation.nationId, nation);
		}
	}

	public void setSectors(List<GWTSector> result) {
		world.setSectors(result);
	}

	public void setCities(List<GWTCity> result) {
		cityMap.clear();
		for (GWTCity city : result) {
			cityMap.put(city.getId(), city);
		}
	}

	public GWTSectorCoords getSelectedCoords() {
		return selectedCoords;
	}
	public void setSelectedCoords(GWTSectorCoords selectedCoords) {
		this.selectedCoords = selectedCoords;
	}
	public GWTWorld getWorld() {
		return world;
	}
	public void setWorld(GWTWorld world) {
		this.world = world;
	}
	public List<GWTUnit> getSelectedUnits() {
		return selectedUnits;
	}
	public Map<Integer, GWTUnit> getUnitMap() {
		return unitMap;
	}
public Map<Integer, GWTNation> getNationMap() {
	return nationMap;
}
	public Map<String, GWTCity> getCityMap() {
		return cityMap;
	}

	public GWTSector[][] getSectors() {
		return world.getSectors();
	}

	public GWTSector getSector(GWTSectorCoords coords) {
		return world.getSector(coords);
	}

	public void setSelectedUnit(GWTUnit selectedUnit) {
		selectedUnits.clear();
		selectedUnits.add(selectedUnit);
	}

	public void setSelectedUnit(int topUnitId) {
		GWTUnit unit = unitMap.get(topUnitId);
		if (unit != null) {
			setSelectedUnit(unit);
		} else {
			StatusReporter.addText("Unit not found: "+topUnitId);
		}
	}

	public void setSelectedUnits(List<GWTUnit> units) {
		selectedUnits.clear();
		selectedUnits.addAll(units);
	}

	public GWTUnit getUnit(int id) {
		return unitMap.get(id);
	}

	public GWTNation getNation(int id) {
		return nationMap.get(id);
	}
	
	public GWTCity getCity(String id) {
		return cityMap.get(id);
	}

	public List<GWTUnit> getSelectedUnits(List<Integer> unitIds) {
		List<GWTUnit> retval = new ArrayList<GWTUnit>();
		for (Integer unitId : unitIds) {
			GWTUnit unit = unitMap.get(unitId);
			if (unit != null) {
				retval.add(unit);
			}
		}
		return retval;
	}

	public GWTNation getNation() {
		return getNation(nationId);
	}

	public void setNationId(int nationId) {
		this.nationId = nationId;
	}
	
	public int getSize() {
		return size;
	}
}
