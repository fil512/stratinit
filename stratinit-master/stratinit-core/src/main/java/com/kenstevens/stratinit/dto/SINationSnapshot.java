package com.kenstevens.stratinit.dto;

public class SINationSnapshot implements StratInitDTO {
	public int tickNumber;
	public String nationName;
	public int cities;
	public int power;
	public double tech;
	public int commandPoints;
	public int capitalPoints;

	public SINationSnapshot() {
	}

	public SINationSnapshot(int tickNumber, String nationName, int cities, int power,
							double tech, int commandPoints, int capitalPoints) {
		this.tickNumber = tickNumber;
		this.nationName = nationName;
		this.cities = cities;
		this.power = power;
		this.tech = tech;
		this.commandPoints = commandPoints;
		this.capitalPoints = capitalPoints;
	}
}
