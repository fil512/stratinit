package com.kenstevens.stratinit.client.site.command.get;

import com.kenstevens.stratinit.client.site.PostCommand;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.SetGameJson;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class JoinGameCommand extends PostCommand<SINation, SetGameJson> {

    public JoinGameCommand(int gameId, boolean noAlliances) {
        super(new SetGameJson(gameId, noAlliances), buildDescription(gameId));
    }

    @Override
    public Result<SINation> executePost(SetGameJson request) {
        return stratInitServer.joinGame(request);
    }

    public static String buildDescription(int gameId) {
        return "Joining Game #" + gameId;
    }

    @Override
    public void handleSuccess(SINation nation) {
    }
}
