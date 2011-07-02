package com.kenstevens.stratinit.model;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.kenstevens.stratinit.type.SectorCoords;


@Entity
public class LaunchedSatellite implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name="launched_id_seq", sequenceName="launched_id_sequence", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="launched_id_seq")
	private Integer satelliteId;
	@ManyToOne
	private Nation nation;
	@Embedded
	private SectorCoords coords;

	public LaunchedSatellite() {}

	public LaunchedSatellite(Nation nation, SectorCoords coords) {
		this.nation = nation;
		this.coords = coords;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((satelliteId == null) ? 0 : satelliteId.hashCode());
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
		LaunchedSatellite other = (LaunchedSatellite) obj;
		if (satelliteId == null) {
			if (other.satelliteId != null)
				return false;
		} else if (!satelliteId.equals(other.satelliteId))
			return false;
		return true;
	}

	public void setSatelliteId(Integer satelliteId) {
		this.satelliteId = satelliteId;
	}

	public Integer getSatelliteId() {
		return satelliteId;
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

	public int getGameId() {
		return nation.getGameId();
	}

}
