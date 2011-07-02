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
public class UnitMove {
	@Id
	@SequenceGenerator(name = "unitmove_id_seq", sequenceName = "unitmove_id_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unitmove_id_seq")
	private Integer id;

	@ManyToOne
	private Unit unit;
	@Embedded
	private SectorCoords coords;
	
	public UnitMove() {}
	
	public UnitMove(Unit unit, SectorCoords nextCoords) {
		this.setUnit(unit);
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

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Unit getUnit() {
		return unit;
	}
}
