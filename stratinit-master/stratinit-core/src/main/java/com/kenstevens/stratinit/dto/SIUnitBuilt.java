package com.kenstevens.stratinit.dto;

import java.io.Serializable;
import java.util.Date;

import com.kenstevens.stratinit.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;


public class SIUnitBuilt implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id;
	public Date date;
	public UnitType type;
	public SectorCoords coords;

	public SIUnitBuilt() {}
	
	public SIUnitBuilt(UnitBuildAudit unitBuildAudit) {
		id = unitBuildAudit.getId();
		date = unitBuildAudit.getDate();
		type = unitBuildAudit.getType();
		coords = new SectorCoords(unitBuildAudit.getX(), unitBuildAudit.getY());
	}
}
