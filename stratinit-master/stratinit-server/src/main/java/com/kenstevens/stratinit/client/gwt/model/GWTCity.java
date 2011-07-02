package com.kenstevens.stratinit.client.gwt.model;

import java.io.Serializable;


public class GWTCity implements Serializable, GWTEntity<String> {
	private static final long serialVersionUID = 1L;
	public GWTSectorCoords coords;
	public GWTCityType cityType;
	public GWTUnitType build;
	public GWTUnitType nextBuild;
	public int nationId;

	public GWTCity() {
	}

	public GWTCity(int x, int y, GWTCityType cityType, int nationId) {
		this.coords = new GWTSectorCoords(x, y);
		this.cityType = cityType;
		this.nationId = nationId;
	}

	public StratInitListGridRecord<String, GWTCity> getListGridRecord() {
		return new CityListGridRecord(this);
	}

	public String getId() {
		return coords.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coords == null) ? 0 : coords.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GWTCity other = (GWTCity) obj;
		if (coords == null) {
			if (other.coords != null)
				return false;
		} else if (!coords.equals(other.coords))
			return false;
		return true;
	}

}
