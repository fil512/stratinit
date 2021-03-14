package com.kenstevens.stratinit.dto;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

import java.util.Date;


public class SICityUpdate implements StratInitDTO {
	private static final long serialVersionUID = 882065030613499056L;
	public SectorCoords coords;
	public CityType type;
	public CityFieldToUpdateEnum field;
	public UnitType build;
	public UnitType nextBuild;
	public int nationId;
	public Date lastUpdated;
	public boolean switchOnTechChange;
	public SectorCoords nextCoords;

	public SICityUpdate() {
	}

	public SICityUpdate(City city) {
		coords = city.getCoords();
		type = city.getType();
		nationId = city.getNation().getNationId();
	}

	public SICityUpdate(City city, SectorCoords nextCoords) {
		this(city);
		this.field = CityFieldToUpdateEnum.NEXT_COORDS;
		this.nextCoords = nextCoords;
	}

	public SICityUpdate(City city, CityFieldToUpdateEnum field, UnitType unitType) {
		this(city);
		this.field = field;
		if (field == CityFieldToUpdateEnum.BUILD) {
			build = unitType;
		} else if (field == CityFieldToUpdateEnum.NEXT_BUILD) {
			nextBuild = unitType;
		}
	}

	public SICityUpdate(int x, int y, CityType type, int nationId) {
		coords = new SectorCoords(x, y);
		this.type = type;
		this.nationId = nationId;
	}

	public SICityUpdate(City city, CityFieldToUpdateEnum field) {
		this(city);
		this.field = field;
		if (field == CityFieldToUpdateEnum.SWITCH_ON_TECH_CHANGE) {
			// FIXME is this right?
			switchOnTechChange = city.isSwitchOnTechChange();
		}
	}

	public void privateData(Nation nation, City city) {
		if (nation.equals(city.getNation())) {
			build = city.getBuild();
			nextBuild = city.getNextBuild();
			switchOnTechChange = city.isSwitchOnTechChange();
			lastUpdated = city.getLastUpdated();
			if (city.getCityMove() != null) {
				nextCoords = city.getCityMove().getCoords();
			}
		}
	}
}
