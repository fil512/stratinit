package com.kenstevens.stratinit.client.gwt.controller;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kenstevens.stratinit.client.gwt.service.GWTGameServiceAsync;
import com.kenstevens.stratinit.client.gwt.service.GWTNone;
import com.kenstevens.stratinit.client.gwt.service.GWTResult;
import com.kenstevens.stratinit.client.gwt.status.GameActions;
import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
import com.kenstevens.stratinit.client.gwt.widget.ModalWindow;
import com.smartgwt.client.widgets.Canvas;

public class GameTableController {
	private final GWTGameServiceAsync gameServiceAsync;
	private final StatusSetter statusSetter;
	private final GameActions gameActions;

	public GameTableController(GWTGameServiceAsync gameServiceAsync,
			GameActions gameActions, StatusSetter statusSetter) {
		this.gameServiceAsync = gameServiceAsync;
		this.statusSetter = statusSetter;
		this.gameActions = gameActions;
	}

	public void asyncAddGame(Canvas canvas, final String gameName, int islands,
			boolean blitz) {
		// TODO SEC check admin
		// Set up the callback object.
		final ModalWindow modalWindow = new ModalWindow(canvas);
		AsyncCallback<GWTResult<GWTNone>> callback = new AsyncCallback<GWTResult<GWTNone>>() {
			public void onFailure(Throwable caught) {
				modalWindow.destroy();
				statusSetter.setText(caught.getMessage());
			}

			public void onSuccess(GWTResult<GWTNone> result) {
				modalWindow.destroy();
				String lastMessage = result.getLastMessage();
				if (result.isSuccess()) {
					statusSetter.setText(lastMessage);
					gameActions.refreshData();
				} else {
					statusSetter.setText(lastMessage);
				}
			}
		};
		if (blitz) {
			modalWindow.show("creating blitz game " + gameName, true);
			gameServiceAsync.createBlitzGame(gameName, islands, callback);
		} else {
			modalWindow.show("creating blitz game " + gameName, true);
			gameServiceAsync.createGame(gameName, callback);
		}
	}

	public void asyncJoinGame(final int gameId) {
		AsyncCallback<GWTResult<Integer>> callback = new AsyncCallback<GWTResult<Integer>>() {
			public void onFailure(Throwable e) {
				statusSetter.setText("Unable to join game " + gameId + ": "
						+ e.getMessage());
			}

			public void onSuccess(GWTResult<Integer> result) {
				if (result.isSuccess()) {
					int island = result.getValue();
					statusSetter.setText("Game " + gameId
							+ " joined.  You are on island #" + island);
					gameActions.refreshData();
				} else {
					statusSetter.setText("Unable to join game " + gameId + ": "
							+ result.getLastMessage());
				}
			}
		};
		gameServiceAsync.joinGame(gameId, callback);
	}



}
