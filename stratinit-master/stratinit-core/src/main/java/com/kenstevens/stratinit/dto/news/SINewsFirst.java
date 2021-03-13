package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.type.NewsCategory;
import com.kenstevens.stratinit.type.UnitType;

public class SINewsFirst extends SINewsNation {
	private static final long serialVersionUID = 1L;
	public UnitType unitType;

	public SINewsFirst() {
	}

	public SINewsFirst(NewsCategory category, String nationName, UnitType unitType) {
		super(nationName, category);
		this.unitType = unitType;
	}

	public SINewsFirst(UnitBuildAudit unitBuildAudit, Nation nation) {
		super(nation, unitBuildAudit);
		this.unitType = unitBuildAudit.getType();
	}
}
