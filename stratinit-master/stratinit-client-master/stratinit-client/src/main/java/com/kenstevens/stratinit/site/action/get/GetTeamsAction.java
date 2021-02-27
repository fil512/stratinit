package com.kenstevens.stratinit.site.action.get;

import com.kenstevens.stratinit.site.GetAction;
import com.kenstevens.stratinit.site.command.GetTeamsCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetTeamsAction extends GetAction<GetTeamsCommand> {
	protected GetTeamsAction() {
		super(new GetTeamsCommand());
	}
}