package com.kenstevens.stratinit.dto.news;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.type.NewsCategory;
import com.kenstevens.stratinit.type.UnitType;

public class SINewsFirst extends SINewsNation {
	private static final long serialVersionUID = 1L;
	public final UnitType unitType;

	// FIXME add creators for all the news types with tests
	@JsonCreator
	public SINewsFirst(@JsonProperty("category") NewsCategory category, @JsonProperty("nationName") String nationName, @JsonProperty("unitType") UnitType unitType) {
		super(nationName, category);
		this.unitType = unitType;
	}

	public SINewsFirst(UnitBuildAudit unitBuildAudit, Nation nation) {
		super(nation, unitBuildAudit);
		this.unitType = unitBuildAudit.getType();
	}
}
