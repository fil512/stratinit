package com.kenstevens.stratinit.wicket.model;

import com.kenstevens.stratinit.wicket.provider.GameUnitsBuilt;
import com.kenstevens.stratinit.wicket.provider.UnitsBuiltProvider;
import org.apache.wicket.model.LoadableDetachableModel;

import java.util.List;


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
