package com.kenstevens.stratinit.model;

public class Rankable {

	private final String name;
	private Double rank;
	private int opponents = 0;
	private int victories = 0;
	private int played = 0;
	private int wins = 0;

	public Rankable(String name, Double rank, int victories, int opponents, int wins, int played) {
		this.name = name;
		this.rank = rank;
		this.victories = victories;
		this.opponents = opponents;
		this.wins = wins;
		this.played = played;
	}

	public Rankable(String name) {
		this.name = name;
	}

	public Double getRank() {
		return rank;
	}

	public void setRank(Double rank) {
		this.rank = rank;
	}

	public void played() {
		++this.played;
	}
	
	public void won() {
		++this.wins;
	}
	
	public void victorious() {
		++this.opponents;
		++this.victories;
	}

	public void defeated() {
		++this.opponents;
	}

	public int getPlayed() {
		return played;
	}
	
	public int getVictories() {
		return victories;
	}
	
	public int getOpponents() {
		return opponents;
	}

	public int getWinPerc() {
		if (played == 0) {
			return 0;
		}
		return 100 * wins / played;
	}

	public int getVicPerc() {
		if (opponents == 0) {
			return 0;
		}
		return 100 * victories / opponents;
	}

	public int getWins() {
		return wins;
	}

	public String getName() {
		return name;
	}

}