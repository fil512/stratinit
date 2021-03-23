package com.kenstevens.stratinit.client.site.command.get;

import com.kenstevens.stratinit.client.site.GetCommand;
import com.kenstevens.stratinit.client.site.processor.GameListProcessor;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetGamesCommand extends GetCommand<List<SIGame>> {
	@Autowired
	private GameListProcessor gameListProcessor;

	@Override
	public Result<List<SIGame>> execute() {
        return stratInitServer.getJoinedGames();
    }

	@Override
	public String getDescription() {
		return "Get nations";
	}

	@Override
	public void handleSuccess(List<SIGame> sigames) {
		gameListProcessor.process(sigames);
	}
}
