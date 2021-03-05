package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.type.RelationType;
import com.querydsl.core.annotations.QueryInit;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Date;


@Entity
public class Relation implements EventKeyed, Serializable {
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	@QueryInit("from.nationPK.game")
	private RelationPK relationPK;
	private RelationType type = RelationType.NEUTRAL;
	private Date switchTime;
	private RelationType nextType;

	public Relation() {}

	public Relation(Nation from, Nation to) {
		this.relationPK = new RelationPK(from, to);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getRelationPK() == null) ? 0 : getRelationPK().hashCode());
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
		Relation other = (Relation) obj;
		if (getRelationPK() == null) {
			return other.getRelationPK() == null;
		} else return getRelationPK().equals(other.getRelationPK());
	}

	public void setType(RelationType type) {
		this.type = type;
	}

	public RelationType getType() {
		return type;
	}

	public Nation getFrom() {
		return getRelationPK().getFrom();
	}

	public Nation getTo() {
		return getRelationPK().getTo();
	}

	public void setSwitchTime(Date switches) {
		this.switchTime = switches;
	}

	public Date getSwitchTime() {
		return switchTime;
	}

	public void setNextType(RelationType nextType) {
		this.nextType = nextType;
	}

	public RelationType getNextType() {
		return nextType;
	}

	public String toString() {
		return getFrom()+" to "+getTo()+": "+type;
	}

	public void setRelationPK(RelationPK relationPK) {
		this.relationPK = relationPK;
	}

	public RelationPK getRelationPK() {
		return relationPK;
	}

	@Override
	public Object getKey() {
		return relationPK;
	}

	// TODO REF use this in other places where we reverse relations like relation changes
	public Relation reverse() {
		return new Relation(getTo(), getFrom());
	}

	public Game getGame() {
		return getFrom().getGame();
	}

	@Override
	public boolean isKeyUnique() {
		return true;
	}
}
