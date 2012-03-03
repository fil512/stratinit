package com.kenstevens.stratinit.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.ManyToOne;

import com.kenstevens.stratinit.type.SectorCoords;


@Embeddable
public class SectorPK implements Serializable {
	private static final long serialVersionUID = -6600370650870811341L;
	@ManyToOne(optional=false)
	private Game game;
	@Embedded
	private SectorCoords coords;

	public SectorPK() {};

	public SectorPK(Game game, SectorCoords coords) {
		this.game = game;
		this.coords = coords;
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
		SectorPK other = (SectorPK) obj;
		if (coords == null) {
			if (other.coords != null)
				return false;
		} else if (!coords.equals(other.coords))
			return false;
		if (game == null) {
			if (other.game != null)
				return false;
		} else if (!game.equals(other.game))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "game ["+game.getId()+"] " + getCoords();
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Game getGame() {
		return game;
	}

	public SectorCoords getCoords() {
		return coords;
	}

	public void setCoords(SectorCoords coords) {
		this.coords = coords;
	}
}
