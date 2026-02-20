package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.type.SectorCoords;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;


@Embeddable
public class SectorSeenPK implements Serializable {
	private static final long serialVersionUID = -8758227151871519439L;
	@ManyToOne(optional=false)
	private Nation nation;
	@Embedded
	private SectorCoords coords;

	SectorSeenPK() {}

	public SectorSeenPK(Nation nation, SectorCoords coords) {
		this.nation = nation;
		this.coords = coords;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coords == null) ? 0 : coords.hashCode());
		result = prime * result + ((nation == null) ? 0 : nation.hashCode());
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
		SectorSeenPK other = (SectorSeenPK) obj;
		if (coords == null) {
			if (other.coords != null)
				return false;
		} else if (!coords.equals(other.coords))
			return false;
		if (nation == null) {
			return other.nation == null;
		} else return nation.equals(other.nation);
	}

	public void setNation(Nation nation) {
		this.nation = nation;
	}
	public Nation getNation() {
		return nation;
	}

	public void setCoords(SectorCoords coords) {
		this.coords = coords;
	}

	public SectorCoords getCoords() {
		return coords;
	}

}
