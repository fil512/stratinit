package com.kenstevens.stratinit.wicket.unit;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;


public class GameUnitsBuiltListModel extends LoadableDetachableModel<List<GameUnitsBuilt>> {
	private static final long serialVersionUID = 1L;

	private final UnitsBuiltProvider unitsBuiltprovider;

	public GameUnitsBuiltListModel(UnitsBuiltProvider unitsBuiltprovider) {
		this.unitsBuiltprovider = unitsBuiltprovider;
	}

	@Override
	protected List<GameUnitsBuilt> load() {
		return unitsBuiltprovider.getUnitsBuilt();
	}

}
