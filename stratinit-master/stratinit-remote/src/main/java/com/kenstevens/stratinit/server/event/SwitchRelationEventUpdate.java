package com.kenstevens.stratinit.server.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.RelationPK;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;

@Scope("prototype")
@Component
public class SwitchRelationEventUpdate extends EventUpdate {
	@Autowired
	private GameDaoService gameDaoService;
	
	private final RelationPK relationPK;

	public SwitchRelationEventUpdate(RelationPK relationPK) {
		this.relationPK = relationPK;
	}

	@Override
	protected void executeWrite() {
		gameDaoService.switchRelation(relationPK);
	}
}