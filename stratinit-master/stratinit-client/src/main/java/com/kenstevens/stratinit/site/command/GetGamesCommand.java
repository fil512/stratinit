package com.kenstevens.stratinit.site.command;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.GameListProcessor;

@Scope("prototype")
@Component
public class GetGamesCommand extends Command<List<SIGame>> {
	@Autowired
	private GameListProcessor gameListProcessor;

	@Override
	public Result<List<SIGame>> execute() {
		return stratInit.getJoinedGames();
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
