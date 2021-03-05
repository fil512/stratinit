package com.kenstevens.stratinit.client.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class GameHistoryNation implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "game_history_nation_id_seq", sequenceName = "game_history_nation_id_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_history_nation_id_seq")
	private Integer id;
	@ManyToOne
	private GameHistoryTeam gameHistoryTeam;
	private String gamename;
	private int cities;
	private int power;

	public GameHistoryNation() {
	}

	public GameHistoryNation(GameHistoryTeam gameHistoryTeam, String gamename, int cities, int power) {
		this.setGameHistoryTeam(gameHistoryTeam);
		this.setGamename(gamename);
		this.setCities(cities);
		this.setPower(power);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameHistoryNation other = (GameHistoryNation) obj;
		if (id == null) {
			return other.id == null;
		} else return id.equals(other.id);
	}

	public void setGameHistoryTeam(GameHistoryTeam gameHistoryTeam) {
		this.gameHistoryTeam = gameHistoryTeam;
	}

	public GameHistoryTeam getGameHistoryTeam() {
		return gameHistoryTeam;
	}

	public void setCities(int cities) {
		this.cities = cities;
	}

	public int getCities() {
		return cities;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public int getPower() {
		return power;
	}

	public String getGamename() {
		return gamename;
	}

	public void setGamename(String gamename) {
		this.gamename = gamename;
	}
}
