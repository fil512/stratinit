package com.kenstevens.stratinit.client.gwt.widget.play.tab;

import com.kenstevens.stratinit.client.gwt.event.GWTCoordsSelectedEventHandler;
import com.kenstevens.stratinit.client.gwt.event.GWTSelectionSource;
import com.kenstevens.stratinit.client.gwt.model.GWTCity;
import com.kenstevens.stratinit.client.gwt.model.GWTSectorCoords;
import com.kenstevens.stratinit.client.gwt.model.GWTUnitType;
import com.kenstevens.stratinit.client.gwt.service.CityBuilder;
import com.kenstevens.stratinit.client.gwt.service.GWTDataManager;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;

public final class BuildPanelControl {
	private BuildPanelControl() {}
	
	public static void addHandlers(final BuildPanel buildPanel) {
		GWTCoordsSelectedEventHandler handler = new GWTCoordsSelectedEventHandler() {

			@Override
			public void coordsSelected(GWTSectorCoords selectedCoords, GWTSelectionSource source) {
				GWTCity city = GWTDataManager.getCity(selectedCoords);
				if (city != null) {
					buildPanel.setBuild(city.build);
					buildPanel.setNextBuild(city.nextBuild);
				}
			}
		};
		GWTDataManager.addHandler(GWTCoordsSelectedEventHandler.TYPE, handler);

		buildPanel.getBuildItem().addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				String value = (String)event.getValue();
				if (value == null) {
					return;
				}
				GWTCity city = GWTDataManager.getCity(GWTDataManager.getSelectedCoords());
				if (city == null) {
					return;
				}
				GWTUnitType build = GWTUnitType.valueOf(value);
				city.build = build;
				CityBuilder cityBuilder = new CityBuilder();
				cityBuilder.setBuild(city);
			}

		});
		buildPanel.getNextBuildItem().addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				String value = (String)event.getValue();
				if (value == null) {
					return;
				}
				GWTCity city = GWTDataManager.getCity(GWTDataManager.getSelectedCoords());
				if (city == null) {
					return;
				}
				GWTUnitType nextBuild = GWTUnitType.valueOf(value);
				city.nextBuild = nextBuild;
				CityBuilder cityBuilder = new CityBuilder();
				cityBuilder.setNextBuild(city);
			}

		});
	}
}
