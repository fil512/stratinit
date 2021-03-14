package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.site.PostAction;
import com.kenstevens.stratinit.client.site.command.post.SetGameCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SetGameAction extends PostAction<SetGameCommand> {
	public SetGameAction(int gameId, boolean noAlliances) {
		super(new SetGameCommand(gameId, noAlliances));
	}
}