package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.SetRelationCommand;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class SetRelationAction extends Action {
	@Autowired
	private Spring spring;
	@Autowired
	private ActionFactory actionFactory;

	private SetRelationCommand setRelationCommand;

	private final Nation nation;
	private final RelationType relationType;

	public SetRelationAction(Nation nation, RelationType relationType) {
		this.nation = nation;
		this.relationType = relationType;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		setRelationCommand = spring.autowire(new SetRelationCommand(nation, relationType));
	}

	@Override
	public Command<? extends Object> getCommand() {
		return setRelationCommand;
	}

	@Override
	public void postRequest() {
		actionFactory.getMap();
	}


}