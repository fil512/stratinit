package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.SetRelationCommand;
import com.kenstevens.stratinit.type.RelationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SetRelationAction extends Action<SetRelationCommand> {
	@Autowired
	private ActionFactory actionFactory;

	private final Nation nation;
	private final RelationType relationType;

	public SetRelationAction(Nation nation, RelationType relationType) {
		this.nation = nation;
		this.relationType = relationType;
	}

	protected SetRelationCommand buildCommand() {
		return new SetRelationCommand(nation, relationType);
	}

	@Override
	public void postRequest() {
		actionFactory.getMap();
	}


}