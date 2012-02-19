package com.kenstevens.stratinit.model.audit;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.news.NewsWorthy;
import com.kenstevens.stratinit.type.NewsCategory;
import com.kenstevens.stratinit.type.UnitType;

@Entity
public class UnitBuildAudit implements NewsWorthy {

	@Id
	@SequenceGenerator(name="unitbuild_id_seq", sequenceName="unitbuild_id_sequence", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="unitbuild_id_seq")
	private Integer id;
	private Date date;
	private int gameId;
	private String username;
	private UnitType type;
	private int x;
	private int y;

	public UnitBuildAudit() {}

	public UnitBuildAudit(Unit unit) {
		this.date = unit.getLastUpdated();
		Nation nation = unit.getNation();
		this.gameId = nation.getGame().getId();
		this.username = nation.getName();
		this.x = unit.getCoords().x;
		this.y = unit.getCoords().y;
		this.type = unit.getType();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setType(UnitType type) {
		this.type = type;
	}

	public UnitType getType() {
		return type;
	}

	@Override
	public NewsCategory getNewsCategory() {
		return NewsCategory.FIRSTS;
	}

}
