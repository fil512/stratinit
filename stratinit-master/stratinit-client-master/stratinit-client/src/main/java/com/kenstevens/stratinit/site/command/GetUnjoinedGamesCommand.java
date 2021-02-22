package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.UnjoinedGameListProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetUnjoinedGamesCommand extends Command<List<SIGame>> {
	@Autowired
	private UnjoinedGameListProcessor gameListProcessor;

	@Override
	public Result<List<SIGame>> execute() {
		return stratInit.getUnjoinedGames();
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
