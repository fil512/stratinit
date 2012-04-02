package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.type.UnitType;

public class SINewsFirst extends SINewsNation {
	private static final long serialVersionUID = 1L;
	public final UnitType unitType;

	public SINewsFirst(UnitBuildAudit unitBuildAudit, Nation nation) {
		super(nation, unitBuildAudit);
		this.unitType = unitBuildAudit.getType();
	}
}
