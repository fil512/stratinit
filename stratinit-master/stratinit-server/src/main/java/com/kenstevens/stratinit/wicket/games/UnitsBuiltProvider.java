package com.kenstevens.stratinit.wicket.games;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.server.remote.helper.UnitsBuilt;
import com.kenstevens.stratinit.server.remote.helper.UnitsBuiltService;

@Service
public class UnitsBuiltProvider {
	@Autowired
	UnitsBuiltService unitsBuiltService;

	public List<UnitsBuilt> getUnitsBuilt() {
		return unitsBuiltService.getUnitsBuilt();
	}

}
