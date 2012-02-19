package com.kenstevens.stratinit.client.gwt.model;

import java.io.Serializable;
import java.util.Date;


public class GWTUnit implements Serializable, GWTEntity<Integer> {
	private static final long serialVersionUID = 1L;
	public GWTSectorCoords coords;
	public GWTUnitType unitType;
	public int id;
	public int nationId;
	public int mobility;
	public int hp;
	public int ammo;
	public int fuel;
	public Date lastUpdated;
	public Date created;
	public GWTUnit() {
	}

	public Integer getId() {
		return id;
	}

	public GWTUnit(int x, int y, GWTUnitType unitType) {
		this.coords = new GWTSectorCoords(x, y);
		this.unitType = unitType;
	}

	public StratInitListGridRecord<Integer, GWTUnit> getListGridRecord() {
		return new UnitListGridRecord(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		GWTUnit other = (GWTUnit) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
