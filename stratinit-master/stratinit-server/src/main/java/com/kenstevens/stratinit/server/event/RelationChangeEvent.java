package com.kenstevens.stratinit.server.event;

import com.kenstevens.stratinit.client.model.Relation;
import com.kenstevens.stratinit.client.model.RelationPK;
import com.kenstevens.stratinit.server.event.svc.StratInitUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
		RelationPK relationPK = (RelationPK) getEventKey().getKey();
		stratInitUpdater.switchRelation(relationPK);
	}
}
