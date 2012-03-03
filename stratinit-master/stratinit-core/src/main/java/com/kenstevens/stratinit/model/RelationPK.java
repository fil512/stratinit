package com.kenstevens.stratinit.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;


@Embeddable
public class RelationPK implements Serializable {
	private static final long serialVersionUID = -8144020314675834151L;
	@ManyToOne(optional=false)
	private Nation from;
	@ManyToOne(optional=false)
	private Nation to;
	
	public RelationPK() {}
	public RelationPK(Nation from, Nation to) {
		this.from = from;
		this.to = to;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
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
		RelationPK other = (RelationPK) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}
	public void setFrom(Nation from) {
		this.from = from;
	}
	public Nation getFrom() {
		return from;
	}
	public void setTo(Nation to) {
		this.to = to;
	}
	public Nation getTo() {
		return to;
	}
	public int getGameId() {
		return from.getGameId();
	}

}
