package com.kenstevens.stratinit.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class GameHistory implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name="game_history_id_seq", sequenceName="game_history_id_sequence", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="game_history_id_seq")
	private Integer id;
	@Column(nullable=false)
    private String name;
	private int gameId;
	private int size;
    private Date startTime;
    private Date ends;
    private int duration = 10; // days
    private boolean blitz = false;

    public GameHistory() {}

    public GameHistory(Game game) {
    	this.setGameId(game.getId());
    	this.name = game.getName();
		this.size = game.getSize();
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
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
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
