package com.kenstevens.stratinit.client.site.command.post;

import com.kenstevens.stratinit.client.site.PostCommand;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.SetGameJson;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SetGameCommand extends PostCommand<None, SetGameJson> {
	public SetGameCommand(int gameId, boolean noAlliances) {
		super(new SetGameJson(gameId, noAlliances), buildDescription(gameId));
	}

	@Override
	public Result<None> executePost(SetGameJson request) {
		return stratInitServer.setGame(request);
	}

	public static String buildDescription(int gameId) {
		return "Switch to Game #" + gameId;
	}

	@Override
	public void handleSuccess(None none) {
	}
}
