package com.kenstevens.stratinit.client.site.action.get;

import com.kenstevens.stratinit.client.site.GetAction;
import com.kenstevens.stratinit.client.site.command.GetUnjoinedGamesCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetUnjoinedGamesAction extends GetAction<GetUnjoinedGamesCommand> {
	protected GetUnjoinedGamesAction() {
		super(new GetUnjoinedGamesCommand());
	}
}