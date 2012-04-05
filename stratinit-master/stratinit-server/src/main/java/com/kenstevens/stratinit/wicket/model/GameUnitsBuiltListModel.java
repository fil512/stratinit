package com.kenstevens.stratinit.wicket.model;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import com.kenstevens.stratinit.wicket.provider.GameUnitsBuilt;
import com.kenstevens.stratinit.wicket.provider.UnitsBuiltProvider;


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
