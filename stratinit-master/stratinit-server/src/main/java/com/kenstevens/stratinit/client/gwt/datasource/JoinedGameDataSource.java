package com.kenstevens.stratinit.client.gwt.datasource;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kenstevens.stratinit.client.gwt.model.GWTGame;
import com.kenstevens.stratinit.client.gwt.service.GWTGameService;
import com.kenstevens.stratinit.client.gwt.status.StatusSetter;

public class JoinedGameDataSource extends GameDataSource {

	public JoinedGameDataSource(StatusSetter statusSetter) {
		super(statusSetter);
	}

	@Override
	protected void fetchGames(AsyncCallback<List<GWTGame>> asyncCallback) {
		GWTGameService.Util.getInstance().getJoinedGames(asyncCallback);
	}
}
