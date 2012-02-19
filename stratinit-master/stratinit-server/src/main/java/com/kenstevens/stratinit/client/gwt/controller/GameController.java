package com.kenstevens.stratinit.client.gwt.controller;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kenstevens.stratinit.client.gwt.service.GWTGameServiceAsync;
import com.kenstevens.stratinit.client.gwt.service.GWTNone;
import com.kenstevens.stratinit.client.gwt.service.GWTResult;
import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
import com.kenstevens.stratinit.client.gwt.widget.GameDisplayer;

public class GameController {
	private final GWTGameServiceAsync gameServiceAsync;
	private final StatusSetter statusSetter;
	
	public GameController(GWTGameServiceAsync gameServiceAsync, StatusSetter statusSetter) {
		this.gameServiceAsync = gameServiceAsync;
		this.statusSetter = statusSetter;
	}
	
	public void asyncGetGameMap(final GameDisplayer gameDisplayer, final int gameId) {
		// Set up the callback object.
		AsyncCallback<GWTResult<String>> callback = new AsyncCallback<GWTResult<String>>() {
			public void onFailure(Throwable e) {
				statusSetter.setText("Unable to get game "+gameId+": "+e.getMessage());
			}

			public void onSuccess(GWTResult<String> result) {
				if (result.success) {
					gameDisplayer.displayGame(result.getValue());
				} else {
					statusSetter.setText("Unable to get game "+gameId+": "+result.getLastMessage());
				}
			}
		};
		gameServiceAsync.getGameMap(gameId, callback);
	}

	public void asyncEndGame(final int gameId) {
		// Set up the callback object.
		AsyncCallback<GWTResult<GWTNone>> callback = new AsyncCallback<GWTResult<GWTNone>>() {
			public void onFailure(Throwable e) {
				statusSetter.setText("Unable to end game "+gameId+": "+e.getMessage());
			}

			public void onSuccess(GWTResult<GWTNone> result) {
				if (result.success) {
					statusSetter.setText("Game "+gameId+" ended.");
				} else {
					statusSetter.setText("Unable to get game "+gameId+": "+result.getLastMessage());
				}
			}
		};
		gameServiceAsync.endGame(gameId, callback);
	}
}
