package com.kenstevens.stratinit.wicket.unit;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import com.kenstevens.stratinit.server.remote.helper.UnitsBuilt;

public class UnitsBuiltListModel extends LoadableDetachableModel<List<UnitsBuilt>> {
	private static final long serialVersionUID = 1L;

	private final UnitsBuiltProvider unitsBuiltprovider;

	public UnitsBuiltListModel(UnitsBuiltProvider unitsBuiltprovider) {
		this.unitsBuiltprovider = unitsBuiltprovider;
	}

	@Override
	protected List<UnitsBuilt> load() {
		return unitsBuiltprovider.getUnitsBuilt();
	}

}
