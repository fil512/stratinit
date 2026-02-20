package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.type.UnitType;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serializable;

@Entity
public class GameBuildAudit implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private Integer gameId;
	private UnitType type;
	private long count;

	public GameBuildAudit() {
	}

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

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
