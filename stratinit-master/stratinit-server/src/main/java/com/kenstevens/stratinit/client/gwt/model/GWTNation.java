package com.kenstevens.stratinit.client.gwt.model;

import java.io.Serializable;


public class GWTNation implements Serializable, GWTEntity<Integer> {
	private static final long serialVersionUID = 1L;
	public static final double UNKNOWN = -1.0;
	public int nationId;
	public String name;
	public double tech = UNKNOWN;
	public int cities;
	public int wins;
	public int played;
	public int power;
	public GWTSectorCoords startCoords;
	public int commandPoints;

	public GWTNation() {
	}

	public GWTNation(int nationId, String name) {
		this.nationId = nationId;
		this.name = name;
	}

	public StratInitListGridRecord<Integer, GWTNation> getListGridRecord() {
		return new NationListGridRecord(this);
	}

	public Integer getId() {
		return nationId;
	}

	public int getNationId() {
		return nationId;
	}

	public String getName() {
		return name;
	}

	public double getTech() {
		return tech;
	}

	public int getCities() {
		return cities;
	}

	public int getWins() {
		return wins;
	}

	public int getPlayed() {
		return played;
	}

	public int getPower() {
		return power;
	}

	public GWTSectorCoords getStartCoords() {
		return startCoords;
	}

	public int getCommandPoints() {
		return commandPoints;
	}
}
