package com.kenstevens.stratinit.client.gwt.tab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kenstevens.stratinit.client.gwt.model.GWTUpdate;
import com.kenstevens.stratinit.client.gwt.model.StatusReporter;
import com.kenstevens.stratinit.client.gwt.service.GWTDataManager;
import com.kenstevens.stratinit.client.gwt.service.GWTGameService;
import com.kenstevens.stratinit.client.gwt.service.GWTGameServiceAsync;
import com.kenstevens.stratinit.client.gwt.service.GWTNone;
import com.kenstevens.stratinit.client.gwt.service.GWTResult;
import com.kenstevens.stratinit.client.gwt.widget.ModalWindow;

public class DataUpdater {
	private static GWTGameServiceAsync gameServiceAsync = GWT
			.create(GWTGameService.class);

	public void asyncEnterGame(final int gameId,
			final WorldDisplayer worldDisplayer) {

		AsyncCallback<GWTResult<GWTNone>> callback = new AsyncCallback<GWTResult<GWTNone>>() {
			public void onFailure(Throwable e) {
				StatusReporter.addText("Unable to enter game " + gameId + ": "
						+ e.getMessage());
			}

			public void onSuccess(GWTResult<GWTNone> result) {
				if (result.isSuccess()) {
					StatusReporter.addText("Game set to "+gameId+".");
					asyncGetUpdate(worldDisplayer);
				} else {
					StatusReporter.addText("Unable to enter game " + gameId
							+ ": " + result.getLastMessage());
				}
			}
		};
		StatusReporter.addText("Setting game to " + gameId + "...");
		gameServiceAsync.setGame(gameId, callback);
	}

	public void asyncGetUpdate(final WorldDisplayer worldDisplayer) {
		final ModalWindow modalWindow = new ModalWindow(worldDisplayer.getWindow());

		AsyncCallback<GWTUpdate> callback = new AsyncCallback<GWTUpdate>() {
			public void onFailure(Throwable e) {
				modalWindow.destroy();
				StatusReporter.addText("Unable to get map: " + e.getMessage());
			}

			public void onSuccess(GWTUpdate result) {
				modalWindow.destroy();
				if (result == null) {
					StatusReporter.addText("Unable to get map.");
				} else {
					StatusReporter.addText("Update received.");
					GameOpener gameOpener = new GameOpener(worldDisplayer.getSize());
					gameOpener.init(result);
				}
			}

		};
		modalWindow.show("Loading game...", true);
		StatusReporter.addText("Getting update...");
		gameServiceAsync.getUpdate(callback);
	}

	
	public void update() {

		AsyncCallback<GWTUpdate> callback = new AsyncCallback<GWTUpdate>() {
			public void onFailure(Throwable e) {
				StatusReporter.addText("Failed to get update: "
						+ e.getMessage());
			}

			public void onSuccess(GWTUpdate result) {
				GWTDataManager.update(result);
			}
		};
		StatusReporter.addText("Getting update...");

		gameServiceAsync.getUpdate(callback);
	}

}
