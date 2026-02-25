package com.kenstevens.stratinit.client.model.audit;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "game_nation_snapshot")
public class GameNationSnapshot {

	@Id
	@SequenceGenerator(name = "gns_id_seq", sequenceName = "game_nation_snapshot_id_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gns_id_seq")
	private Integer id;
	private int gameId;
	private String nationName;
	private Date tickTime;
	private int tickNumber;
	private int cities;
	private int power;
	private double tech;
	private int commandPoints;
	private int capitalPoints;

	public GameNationSnapshot() {
	}

	public GameNationSnapshot(int gameId, String nationName, Date tickTime, int tickNumber,
							  int cities, int power, double tech, int commandPoints, int capitalPoints) {
		this.gameId = gameId;
		this.nationName = nationName;
		this.tickTime = tickTime;
		this.tickNumber = tickNumber;
		this.cities = cities;
		this.power = power;
		this.tech = tech;
		this.commandPoints = commandPoints;
		this.capitalPoints = capitalPoints;
	}

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public int getGameId() { return gameId; }
	public void setGameId(int gameId) { this.gameId = gameId; }
	public String getNationName() { return nationName; }
	public void setNationName(String nationName) { this.nationName = nationName; }
	public Date getTickTime() { return tickTime; }
	public void setTickTime(Date tickTime) { this.tickTime = tickTime; }
	public int getTickNumber() { return tickNumber; }
	public void setTickNumber(int tickNumber) { this.tickNumber = tickNumber; }
	public int getCities() { return cities; }
	public void setCities(int cities) { this.cities = cities; }
	public int getPower() { return power; }
	public void setPower(int power) { this.power = power; }
	public double getTech() { return tech; }
	public void setTech(double tech) { this.tech = tech; }
	public int getCommandPoints() { return commandPoints; }
	public void setCommandPoints(int commandPoints) { this.commandPoints = commandPoints; }
	public int getCapitalPoints() { return capitalPoints; }
	public void setCapitalPoints(int capitalPoints) { this.capitalPoints = capitalPoints; }
}
