package com.kenstevens.stratinit.model;

public class Rankable {

	private Double rank;
	private int played = 0;
	private int wins = 0;

	public Rankable() {
		super();
	}

	public Rankable(Double rank, int played, int wins) {
		this.rank = rank;
		this.played = played;
		this.wins = wins;
	}

	public Double getRank() {
		return rank;
	}

	public void setRank(Double rank) {
		this.rank = rank;
	}

	public void won() {
		++this.played;
		++this.wins;
	}

	public void lost() {
		++this.played;
	}

	public int getPlayed() {
		return played;
	}

	public int getWinPerc() {
		if (played == 0) {
			return 0;
		}
		return 100 * getWins() / played;
	}

	public int getWins() {
		return wins;
	}
}