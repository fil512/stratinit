package com.kenstevens.stratinit.world;

public class WorldConfig {
	private int portsPerPlayerIsland = 2;
	private int playerIslandSize = 50;
	private int minDistanceEntreIslands = 4;
	private int minDistanceEntreCities = 2;
	private int minContinentSize = 30;
	private int maxContinentSize = 200;
	private int minPercentCities = 5;
	private int maxPercentCities = 7;
	private int percentWater = 85;
	private int startDistanceEntreContinents = 15;
	private int spikiness = 40; // percent chance next sector grows from same location as last growth
	public void setPortsPerPlayerIsland(int portsPerPlayerIsland) {
		this.portsPerPlayerIsland = portsPerPlayerIsland;
	}
	public int getPortsPerPlayerIsland() {
		return portsPerPlayerIsland;
	}
	public int getPlayerIslandSize() {
		return playerIslandSize;
	}
	public void setPlayerIslandSize(int playerIslandSize) {
		this.playerIslandSize = playerIslandSize;
	}
	public int getMinDistanceEntreIslands() {
		return minDistanceEntreIslands;
	}
	public void setMinDistanceEntreIslands(int minDistanceEntreIslands) {
		this.minDistanceEntreIslands = minDistanceEntreIslands;
	}
	public int getMinDistanceEntreCities() {
		return minDistanceEntreCities;
	}
	public void setMinDistanceEntreCities(int minDistanceEntreCities) {
		this.minDistanceEntreCities = minDistanceEntreCities;
	}
	public int getMinContinentSize() {
		return minContinentSize;
	}
	public void setMinContinentSize(int minContinentSize) {
		this.minContinentSize = minContinentSize;
	}
	public int getMaxContinentSize() {
		return maxContinentSize;
	}
	public void setMaxContinentSize(int maxContinentSize) {
		this.maxContinentSize = maxContinentSize;
	}
	public int getMinPercentCities() {
		return minPercentCities;
	}
	public void setMinPercentCities(int minPercentCities) {
		this.minPercentCities = minPercentCities;
	}
	public int getMaxPercentCities() {
		return maxPercentCities;
	}
	public void setMaxPercentCities(int maxPercentCities) {
		this.maxPercentCities = maxPercentCities;
	}
	public int getPercentWater() {
		return percentWater;
	}
	public void setPercentWater(int percentWater) {
		this.percentWater = percentWater;
	}
	public int getStartDistanceEntreContinents() {
		return startDistanceEntreContinents;
	}
	public void setStartDistanceEntreContinents(int startDistanceEntreContinents) {
		this.startDistanceEntreContinents = startDistanceEntreContinents;
	}
	public int getSpikiness() {
		return spikiness;
	}
	public void setSpikiness(int spikiness) {
		this.spikiness = spikiness;
	}

}
