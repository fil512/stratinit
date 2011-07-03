package com.kenstevens.stratinit.dto;

import java.io.Serializable;


public class SITeam implements Serializable {
	private static final long serialVersionUID = 1L;
	// TODO REF generalize to support multiple players on a team
	public String nation1;
	public String nation2;
	public int score;

	public SITeam() {}

	public SITeam(String nation1, String nation2, int score) {
		this.nation1 = nation1;
		this.nation2 = nation2;
		this.score = score;
	}
	
	public String getNation1() {
		return nation1;
	}
	
	public String getNation2() {
		return nation2;
	}
	
	public int getScore() {
		return score;
	}
}
