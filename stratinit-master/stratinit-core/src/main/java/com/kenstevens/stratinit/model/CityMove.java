package com.kenstevens.stratinit.model;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.kenstevens.stratinit.type.SectorCoords;

@Entity
public class CityMove {
	@Id
	@SequenceGenerator(name = "citymove_id_seq", sequenceName = "citymove_id_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "citymove_id_seq")
	private Integer id;

	@ManyToOne
	private City city;
	@Embedded
	private SectorCoords coords;
	
	public CityMove() {}
	
	public CityMove(City city, SectorCoords nextCoords) {
		this.setCity(city);
		this.coords = nextCoords;
	}

	@SuppressWarnings("unused")
	private void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}
	public void setCoords(SectorCoords coords) {
		this.coords = coords;
	}
	public SectorCoords getCoords() {
		return coords;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public City getCity() {
		return city;
	}
}
