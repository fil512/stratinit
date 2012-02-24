package com.kenstevens.stratinit.model;


public class PlayerRank extends Rankable {
	final String name;
	public PlayerRank(String name, Double rank, int played, int wins) {
		super(rank, played, wins);
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
