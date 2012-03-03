package com.kenstevens.stratinit.model.stats;

public class Preservation {
	protected int built;
	protected int lost;
	public int getStanding() {
		return built - lost;
	}
	public int getPreservation() {
		if (built == 0) {
			return 100;
		}
		return 100 * getStanding() / built;
	}
	public void add(Preservation preservation) {
		built += preservation.built;
		lost += preservation.lost;
	}
}
