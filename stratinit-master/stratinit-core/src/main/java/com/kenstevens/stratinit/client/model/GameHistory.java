package com.kenstevens.stratinit.client.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class GameHistory implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "game_history_id_seq", sequenceName = "game_history_id_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_history_id_seq")
	private Integer id;
	@Column(nullable = false)
	private String gamename;
	private int gameId;
	private int gamesize;
	private Date startTime;
	private Date ends;
	private int duration = 10; // days
	private boolean blitz = false;

	public GameHistory() {
	}

	public GameHistory(Game game) {
		this.setGameId(game.getId());
		this.gamename = game.getGamename();
		this.gamesize = game.getGamesize();
		this.startTime = game.getStartTime();
		this.ends = game.getEnds();
		this.duration = game.getDuration();
		this.blitz = game.isBlitz();
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
		GameHistory other = (GameHistory) obj;
		if (id == null) {
			return other.id == null;
		} else return id.equals(other.id);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getGamename() {
		return gamename;
	}

	public void setGamename(String gamename) {
		this.gamename = gamename;
	}

	public int getGamesize() {
		return gamesize;
	}

	public void setGamesize(int gamesize) {
		this.gamesize = gamesize;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEnds() {
		return ends;
	}

	public void setEnds(Date ends) {
		this.ends = ends;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public boolean isBlitz() {
		return blitz;
	}

	public void setBlitz(boolean blitz) {
		this.blitz = blitz;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getGameId() {
		return gameId;
	}


}
