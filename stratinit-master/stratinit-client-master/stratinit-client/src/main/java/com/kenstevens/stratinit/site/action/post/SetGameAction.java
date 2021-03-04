package com.kenstevens.stratinit.site.action.post;

import com.kenstevens.stratinit.remote.request.SetGameJson;
import com.kenstevens.stratinit.site.PostAction;
import com.kenstevens.stratinit.site.command.SetGameCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SetGameAction extends PostAction<SetGameCommand> {
	public SetGameAction(SetGameJson request) {
		super(new SetGameCommand(request));
	}
}