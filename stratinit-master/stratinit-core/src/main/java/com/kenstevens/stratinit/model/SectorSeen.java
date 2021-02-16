package com.kenstevens.stratinit.model;

import com.kenstevens.stratinit.type.SectorCoords;
import com.querydsl.core.annotations.QueryInit;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Date;

@Entity
public class SectorSeen implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	@QueryInit("nation.nationPK.game")
	private SectorSeenPK sectorSeenPK;

	private Date lastSeen;
	public SectorSeen() {}
	public SectorSeen(Nation nation, SectorCoords coords) {
		sectorSeenPK = new SectorSeenPK(nation, coords);
		lastSeen = new Date();
	}
	public SectorSeen(Nation nation, Sector sector) {
		this(nation, sector.getCoords());
	}
	public void setLastSeen(Date lastSeen) {
		this.lastSeen = lastSeen;
	}
	public Date getLastSeen() {
		return lastSeen;
	}
	public SectorCoords getCoords() {
		return sectorSeenPK.getCoords();
	}

	public Nation getNation() {
		return sectorSeenPK.getNation();
	}

	public SectorSeenPK getSectorSeenPK() {
		return sectorSeenPK;
	}
	public String toString() {
		return getNation()+" "+getCoords();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sectorSeenPK == null) ? 0 : sectorSeenPK.hashCode());
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
		SectorSeen other = (SectorSeen) obj;
		if (sectorSeenPK == null) {
			return other.sectorSeenPK == null;
		} else return sectorSeenPK.equals(other.sectorSeenPK);
	}
	
}
