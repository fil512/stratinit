package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.type.SectorCoords;
import com.querydsl.core.annotations.QueryInit;

import jakarta.persistence.*;

@Entity
public class UnitMove {
	@Id
	@SequenceGenerator(name = "unitmove_id_seq", sequenceName = "unitmove_id_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unitmove_id_seq")
	private Integer id;

	@ManyToOne
	@QueryInit("nation.nationPK.game")
	private Unit unit;
	@Embedded
	private SectorCoords coords;
	
	public UnitMove() {}
	
	public UnitMove(Unit unit, SectorCoords nextCoords) {
		this.setUnit(unit);
		this.coords = nextCoords;
	}

	public void setId(Integer id) {
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
