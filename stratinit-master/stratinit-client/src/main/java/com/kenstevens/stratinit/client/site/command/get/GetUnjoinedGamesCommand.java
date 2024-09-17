package com.kenstevens.stratinit.client.site.command.get;

import com.kenstevens.stratinit.client.site.GetCommand;
import com.kenstevens.stratinit.client.site.processor.UnjoinedGameListProcessor;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetUnjoinedGamesCommand extends GetCommand<List<SIGame>> {
	@Autowired
	private UnjoinedGameListProcessor gameListProcessor;

	@Override
	public Result<List<SIGame>> execute() {
        return stratInitServer.getUnjoinedGames();
    }

	@Override
	public String getDescription() {
		return "Get unjoined games";
	}

	@Override
	public void handleSuccess(List<SIGame> sigames) {
		gameListProcessor.process(sigames);
	}
}
