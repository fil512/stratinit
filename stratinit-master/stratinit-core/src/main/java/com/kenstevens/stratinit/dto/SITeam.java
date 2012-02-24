package com.kenstevens.stratinit.dto;

import java.io.Serializable;

public class SITeam implements Serializable {
	private static final long serialVersionUID = 1L;
	// TODO REF generalize to support multiple players on a team
	public String nation1;
	public String nation2;
	public String name;
	public int score;

	public SITeam() {
	}

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

	public String getName() {
		if (name == null) {
			name = buildName();
		}
		return name;
	}

	private String buildName() {
		if (nation2 == null) {
			return nation1;
		}
		if (nation1.compareTo(nation2) < 0) {
			return nation1 + " " + nation2;
		}
		return nation2 + " " + nation1;
	}

	@Override
	public int hashCode() {
		getName();
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		getName();
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SITeam other = (SITeam) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.getName()))
			return false;
		return true;
	}
}
