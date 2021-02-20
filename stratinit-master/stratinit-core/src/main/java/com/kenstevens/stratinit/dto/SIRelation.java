package com.kenstevens.stratinit.dto;

import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.type.RelationType;

import java.util.Date;


public class SIRelation implements StratInitDTO {
    private static final long serialVersionUID = 1L;
    public int nationId;
    public RelationType meToThem;
    public RelationType myNextType;
    public Date mineSwitches;
    public RelationType themToMe;
    public RelationType theirNextType;
    public Date theirsSwitches;

    public SIRelation() {
    }
	
	public SIRelation(Relation relation, Relation theirRelation) {
		nationId = relation.getTo().getNationId();
		meToThem = relation.getType();
		this.themToMe = theirRelation.getType();
		if (relation.getSwitchTime() != null) {
			mineSwitches = relation.getSwitchTime();
			myNextType = relation.getNextType();
		}
		if (theirRelation.getSwitchTime() != null) {
			theirsSwitches = theirRelation.getSwitchTime();
			theirNextType = theirRelation.getNextType();
		}
	}
}
