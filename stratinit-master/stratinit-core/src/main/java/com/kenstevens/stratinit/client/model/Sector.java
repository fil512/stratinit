package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;


@Entity
public class Sector implements Serializable {

	private static final long serialVersionUID = 1L;
	@EmbeddedId
	public SectorPK sectorPK;
	@Column(nullable=false)
	protected SectorType type;
	private int island = Constants.UNASSIGNED;
	
	public Sector() {}
	
	public Sector(Game game, SectorCoords coords, SectorType type) {
		this.sectorPK = new SectorPK(game, coords);
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sectorPK == null) ? 0 : sectorPK.hashCode());
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
		Sector other = (Sector) obj;
		if (sectorPK == null) {
			return other.sectorPK == null;
		} else return sectorPK.equals(other.sectorPK);
	}
	
	@Override
	public String toString() {
		return sectorPK.toString();
	}

	public SectorCoords getCoords() {
		return sectorPK.getCoords();
	}
	
	public SectorType getType() {
		return type;
	}

	public void setType(SectorType type) {
		this.type = type;
	}

	public void setIsland(int island) {
		this.island = island;
	}

	public int getIsland() {
		return island;
	}

	public Game getGame() {
		return sectorPK.getGame();
	}

	public boolean isWater() {
		return type == SectorType.WATER;
	}

	public boolean isLand() {
		return type == SectorType.LAND;
	}

	public boolean isPlayerCity() {
		return type == SectorType.PLAYER_CITY;
	}

	public boolean adjacentTo(CoordMeasure coordMeasure, Sector sector) {
		return getCoords().adjacentTo(coordMeasure, sector.getCoords());
	}

	public int getX() {
		return getCoords().x;
	}

	public int getY() {
		return getCoords().y;
	}

	public int getGameId() {
		return getGame().getId();
	}

	public boolean isUnknown() {
		return type == SectorType.UNKNOWN;
	}
}
