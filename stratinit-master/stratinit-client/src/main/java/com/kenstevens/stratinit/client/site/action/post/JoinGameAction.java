package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.site.PostAction;
import com.kenstevens.stratinit.client.site.command.get.JoinGameCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class JoinGameAction extends PostAction<JoinGameCommand> {
    public JoinGameAction(int gameId, boolean noAlliances) {
            super(new JoinGameCommand(gameId, noAlliances));
        }
}