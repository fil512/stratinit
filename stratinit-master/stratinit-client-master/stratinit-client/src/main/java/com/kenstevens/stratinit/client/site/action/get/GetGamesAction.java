package com.kenstevens.stratinit.client.site.action.get;

import com.kenstevens.stratinit.client.site.GetAction;
import com.kenstevens.stratinit.client.site.command.get.GetGamesCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetGamesAction extends GetAction<GetGamesCommand> {
	protected GetGamesAction() {
		super(new GetGamesCommand());
	}
}