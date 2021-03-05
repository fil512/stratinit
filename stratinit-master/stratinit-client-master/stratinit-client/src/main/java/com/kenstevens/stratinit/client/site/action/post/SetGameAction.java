package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.site.PostAction;
import com.kenstevens.stratinit.client.site.command.SetGameCommand;
import com.kenstevens.stratinit.remote.request.SetGameJson;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SetGameAction extends PostAction<SetGameCommand> {
	public SetGameAction(SetGameJson request) {
		super(new SetGameCommand(request));
	}
}