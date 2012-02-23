package com.kenstevens.stratinit.model;

public class PlayerRank {
	private final String name;
	private final Double rank;
	
	public PlayerRank(String name, Double rank) {
		this.name = name;
		this.rank = rank;
	}

	public String getName() {
		return name;
	}

	public Double getRank() {
		return rank;
	}
}
