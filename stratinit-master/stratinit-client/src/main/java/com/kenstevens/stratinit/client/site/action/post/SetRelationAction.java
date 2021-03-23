package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.site.PostAction;
import com.kenstevens.stratinit.client.site.command.post.SetRelationCommand;
import com.kenstevens.stratinit.type.RelationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SetRelationAction extends PostAction<SetRelationCommand> {
	@Autowired
	private ActionFactory actionFactory;

	public SetRelationAction(Nation nation, RelationType relationType) {
		super(new SetRelationCommand(nation, relationType));
	}

	@Override
	public void postRequest() {
		actionFactory.getMap();
	}
}