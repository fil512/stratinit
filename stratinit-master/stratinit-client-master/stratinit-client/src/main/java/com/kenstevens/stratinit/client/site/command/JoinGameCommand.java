package com.kenstevens.stratinit.client.site.command;

import com.kenstevens.stratinit.client.site.Command;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.SetGameJson;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class JoinGameCommand extends Command<SINation> {
    private final SetGameJson request;

    public JoinGameCommand(SetGameJson request) {
        this.request = request;
    }

    @Override
    public Result<SINation> execute() {
        return stratInitServer.joinGame(request);
    }

    @Override
    public String getDescription() {
        return "Joining Game #" + request.gameId;
    }

    @Override
    public void handleSuccess(SINation nation) {
    }
}
