package com.kenstevens.stratinit.site.action.post;

import com.kenstevens.stratinit.remote.request.SetGameJson;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.JoinGameCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class JoinGameAction extends Action<JoinGameCommand> {

    private final SetGameJson request;

    public JoinGameAction(SetGameJson request) {
        this.request = request;
    }

    protected JoinGameCommand buildCommand() {
        return new JoinGameCommand(request);
    }
}