package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.type.SectorCoords;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.ManyToOne;
import java.io.Serializable;


@Embeddable
public class CityPK implements Serializable {

	private static final long serialVersionUID = 3902271018638112723L;
	@ManyToOne(optional=false)
	private Game game;
	@Embedded
	private SectorCoords coords;

	public CityPK() {}

	public CityPK(Game game, SectorCoords coords) {
		this.game = game;
		this.coords = coords;
	}

	public CityPK(Sector sector) {
		this(sector.getGame(), sector.getCoords());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coords == null) ? 0 : coords.hashCode());
		result = prime * result + ((game == null) ? 0 : game.hashCode());
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
		CityPK other = (CityPK) obj;
		if (coords == null) {
			if (other.coords != null)
				return false;
		} else if (!coords.equals(other.coords))
			return false;
		if (game == null) {
			return other.game == null;
		} else return game.equals(other.game);
	}

	public int getGameId() {
		return game.getId();
	}

	public SectorCoords getCoords() {
		return coords;
	}
}
