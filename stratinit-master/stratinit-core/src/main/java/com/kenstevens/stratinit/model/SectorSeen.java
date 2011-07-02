package com.kenstevens.stratinit.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.kenstevens.stratinit.type.SectorCoords;

@Entity
public class SectorSeen implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
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
}
