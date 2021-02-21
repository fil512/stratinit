package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.GetRelationsCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetRelationsAction extends Action<GetRelationsCommand> {
	protected GetRelationsCommand buildCommand() {
		return new GetRelationsCommand();
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}