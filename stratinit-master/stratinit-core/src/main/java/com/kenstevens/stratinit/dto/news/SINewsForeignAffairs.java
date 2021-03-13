package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.audit.RelationChangeAudit;
import com.kenstevens.stratinit.type.RelationType;

import java.util.Date;

public class SINewsForeignAffairs extends SINewsOpponents {
    private static final long serialVersionUID = 1L;
    public RelationType oldRelation;
    public RelationType newRelation;
    public Date effective;

    public SINewsForeignAffairs() {
    }

    public SINewsForeignAffairs(RelationChangeAudit relationChangeAudit, Nation player, Nation opponent) {
        super(player, opponent, relationChangeAudit);
        oldRelation = relationChangeAudit.getType();
        newRelation = relationChangeAudit.getNextType();
        effective = relationChangeAudit.getEffective();
    }
}
