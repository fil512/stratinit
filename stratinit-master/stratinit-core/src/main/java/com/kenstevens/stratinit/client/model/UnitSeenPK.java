package com.kenstevens.stratinit.client.model;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;


@Embeddable
public class UnitSeenPK implements Serializable {
	private static final long serialVersionUID = 1L;
	@ManyToOne
	private Nation nation;
	@ManyToOne
	private Unit unit;

	UnitSeenPK() {}

	public UnitSeenPK(Nation nation, Unit unit) {
		this.nation = nation;
		this.unit = unit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nation == null) ? 0 : nation.hashCode());
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
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
		UnitSeenPK other = (UnitSeenPK) obj;
		if (nation == null) {
			if (other.nation != null)
				return false;
		} else if (!nation.equals(other.nation))
			return false;
		if (unit == null) {
			return other.unit == null;
		} else return unit.equals(other.unit);
	}

	public void setNation(Nation nation) {
		this.nation = nation;
	}
	public Nation getNation() {
		return nation;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Unit getUnit() {
		return unit;
	}

	public int getGameId() {
		return unit.getGameId();
	}



}
