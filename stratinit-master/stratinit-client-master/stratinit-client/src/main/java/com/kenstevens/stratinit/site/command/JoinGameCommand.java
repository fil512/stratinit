package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.SetGameRequest;
import com.kenstevens.stratinit.site.Command;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class JoinGameCommand extends Command<Nation> {
    private final SetGameRequest request;

    public JoinGameCommand(SetGameRequest request) {
        this.request = request;
    }

    @Override
    public Result<Nation> execute() {
        return stratInitServer.joinGame(request);
    }

    @Override
    public String getDescription() {
        return "Joining Game #" + request.gameId;
    }

	@Override
	public void handleSuccess(Nation nation) {
	}
}
