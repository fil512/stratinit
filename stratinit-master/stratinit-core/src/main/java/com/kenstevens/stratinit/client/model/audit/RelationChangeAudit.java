package com.kenstevens.stratinit.client.model.audit;

import com.kenstevens.stratinit.client.model.Relation;
import com.kenstevens.stratinit.news.NewsWorthy;
import com.kenstevens.stratinit.type.NewsCategory;
import com.kenstevens.stratinit.type.RelationType;

import javax.persistence.*;
import java.util.Date;

@Entity
public class RelationChangeAudit implements NewsWorthy {

	@Id
	@SequenceGenerator(name = "relationchange_id_seq", sequenceName = "relationchange_id_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "relationchange_id_seq")
	private Integer id;
	private Date date;
	private Date effective;
	private int gameId;
	private String fromUsername;
	private String toUsername;
	private RelationType type;
	private RelationType nextType;

	public RelationChangeAudit() {
	}

	public RelationChangeAudit(Relation relation) {
		this.date = new Date();
		this.effective = relation.getSwitchTime();
		this.gameId = relation.getFrom().getGameId();
		this.fromUsername = relation.getFrom().getName();
		this.toUsername = relation.getTo().getName();
		this.type = relation.getType();
		this.nextType = relation.getNextType();
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

	public Date getEffective() {
		return effective;
	}

	public void setEffective(Date effective) {
		this.effective = effective;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getFromUsername() {
		return fromUsername;
	}

	public void setFromUsername(String fromUsername) {
		this.fromUsername = fromUsername;
	}

	public String getToUsername() {
		return toUsername;
	}

	public void setToUsername(String toUsername) {
		this.toUsername = toUsername;
	}

	public RelationType getType() {
		return type;
	}

	public void setType(RelationType type) {
		this.type = type;
	}

	public RelationType getNextType() {
		return nextType;
	}

	public void setNextType(RelationType nextType) {
		this.nextType = nextType;
	}


	@Override
	public NewsCategory getNewsCategory() {
		return NewsCategory.FOREIGN_AFFAIRS;
	}
}
