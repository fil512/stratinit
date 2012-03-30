package com.kenstevens.stratinit.wicket.unit;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UnitsBuiltProvider {
	@Autowired
	UnitsBuiltService unitsBuiltService;

	public List<GameUnitsBuilt> getUnitsBuilt() {
		return unitsBuiltService.getUnitsBuilt();
	}

}
