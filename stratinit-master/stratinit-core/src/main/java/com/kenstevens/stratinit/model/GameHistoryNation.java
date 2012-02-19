package com.kenstevens.stratinit.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class GameHistoryNation implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name="game_history_nation_id_seq", sequenceName="game_history_nation_id_sequence", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="game_history_nation_id_seq")
	private Integer id;
	@ManyToOne
	private GameHistoryTeam gameHistoryTeam;
	private String name;
	private int cities;
	private int power;

    public GameHistoryNation() {}

    public GameHistoryNation(GameHistoryTeam gameHistoryTeam, String name, int cities, int power) {
    	this.setGameHistoryTeam(gameHistoryTeam);
    	this.setName(name);
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
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
