package com.kenstevens.stratinit.server.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.model.RelationPK;

@Scope("prototype")
@Component
public class RelationChangeEvent extends Event {
	@Autowired
	private StratInitUpdater stratInitUpdater;

	public RelationChangeEvent(Relation relation) {
		super(relation, relation.getSwitchTime());
	}

	@Override
	protected void execute() {
		RelationPK relationPK = (RelationPK)getEventKey().getKey();
		stratInitUpdater.switchRelation(relationPK);
	}
}
