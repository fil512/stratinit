package com.kenstevens.stratinit.dto.news;

import java.util.Date;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.audit.RelationChangeAudit;
import com.kenstevens.stratinit.type.RelationType;

public class SINewsForeignAffairs extends SINewsOpponents {
	private static final long serialVersionUID = 1L;
	public final RelationType oldRelation;
	public final RelationType newRelation;
	public final Date effective;

	public SINewsForeignAffairs(RelationChangeAudit relationChangeAudit, Nation player, Nation opponent) {
		super(player, opponent, relationChangeAudit);
		oldRelation = relationChangeAudit.getType();
		newRelation = relationChangeAudit.getNextType();
		effective = relationChangeAudit.getEffective();
	}
}
