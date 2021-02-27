package com.kenstevens.stratinit.site.action.get;

import com.kenstevens.stratinit.site.GetAction;
import com.kenstevens.stratinit.site.command.GetGamesCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetGamesAction extends GetAction<GetGamesCommand> {
	protected GetGamesAction() {
		super(new GetGamesCommand());
	}
}