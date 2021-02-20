package com.kenstevens.stratinit.dto;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

import java.util.Date;


public class SICity implements StratInitDTO {
	private static final long serialVersionUID = 882065030613499056L;
	public SectorCoords coords;
	public CityType type;
	public UnitType build;
	public UnitType nextBuild;
	public int nationId;
	public Date lastUpdated;
	public boolean switchOnTechChange;
	public SectorCoords nextCoords;

	public SICity() {}

	public SICity(City city) {
		coords = city.getCoords();
		type = city.getType();
		nationId = city.getNation().getNationId();
	}

	public SICity(int x, int y, CityType type, int nationId) {
		coords = new SectorCoords(x, y);
		this.type = type;
		this.nationId = nationId;
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
