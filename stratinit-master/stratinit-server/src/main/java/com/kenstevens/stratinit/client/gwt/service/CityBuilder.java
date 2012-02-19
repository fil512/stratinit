package com.kenstevens.stratinit.client.gwt.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kenstevens.stratinit.client.gwt.model.GWTCity;
import com.kenstevens.stratinit.client.gwt.model.GWTUpdate;
import com.kenstevens.stratinit.client.gwt.model.StatusReporter;

public class CityBuilder {
	private static GWTGameServiceAsync gameServiceAsync = GWT.create(GWTGameService.class);


	public void setBuild(final GWTCity city) {
		AsyncCallback<GWTResult<GWTUpdate>> callback = new AsyncCallback<GWTResult<GWTUpdate>>() {
			public void onFailure(Throwable e) {
				StatusReporter.addText("Failed to set build in " + city.coords + ": "
						+ e.getMessage());
			}

			public void onSuccess(GWTResult<GWTUpdate> result) {
				if (result.isSuccess()) {
					StatusReporter.addText("Build set to " + city.build + " in "+city.coords+".");
					GWTDataManager.update(result.getValue());
				} else {
					StatusReporter.addText("Unable to set build in" + city.coords
							+ ": " + result.getLastMessage());
				}
			}
		};
		StatusReporter.addText("Setting build in "
				+ city.coords+" to "+city.build+".");

		gameServiceAsync.updateCity(city, true, callback);
	}

	public void setNextBuild(final GWTCity city) {
		AsyncCallback<GWTResult<GWTUpdate>> callback = new AsyncCallback<GWTResult<GWTUpdate>>() {
			public void onFailure(Throwable e) {
				StatusReporter.addText("Failed to set next build in " + city.coords + ": "
						+ e.getMessage());
			}

			public void onSuccess(GWTResult<GWTUpdate> result) {
				if (result.isSuccess()) {
					StatusReporter.addText("Next build set to " + city.build + " in "+city.coords+".");
					GWTDataManager.update(result.getValue());
				} else {
					StatusReporter.addText("Unable to set next build in" + city.coords
							+ ": " + result.getLastMessage());
				}
			}
		};
		StatusReporter.addText("Setting next build in "
				+ city.coords+" to "+city.nextBuild+".");

		gameServiceAsync.updateCity(city, false, callback);
	}

}
