package com.kenstevens.stratinit.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.kenstevens.stratinit.type.UnitType;

@Entity
public class GameBuildAudit implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private Integer gameId;
    private UnitType type;
	private int count;
    
    public GameBuildAudit() {}

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}

	public UnitType getType() {
		return type;
	}

	public void setType(UnitType type) {
		this.type = type;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
