package com.kenstevens.stratinit.model.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kenstevens.stratinit.dto.SIUnitBuilt;
import com.kenstevens.stratinit.type.UnitType;

public class UnitPreservationMap {
	
	private final Map<UnitType, Preservation> unitPreservation = new HashMap<UnitType, Preservation>();

	protected List<String[]> getUnitRecordStats() {
		List<String[]> retval = new ArrayList<String[]>();
		Preservation total = new Preservation();
		for (UnitType unitType : unitPreservation.keySet()) {
			Preservation preservation = unitPreservation.get(unitType);
			total.add(preservation);
			String[] line = new String[] {unitType.toString().toLowerCase(), ""+preservation.built,
					""+preservation.lost, ""+preservation.getStanding(), ""+preservation.getPreservation()+"%"};
			retval.add(line);
		}
		String[] line = new String[] {"TOTAL", ""+total.built,
				""+total.lost, ""+total.getStanding(), ""+total.getPreservation()+"%"};
		retval.add(line);
		return retval;
	}

	protected void clear() {
		unitPreservation.clear();
	}

	protected Preservation getPreservation(UnitType unitType) {
		Preservation preservation = unitPreservation.get(unitType);

		if (preservation == null) {
			preservation = new Preservation();
			unitPreservation.put(unitType, preservation);
		}
		return preservation;
	}


	protected void addBuilt(List<SIUnitBuilt> siUnitsBuilt) {
		for (SIUnitBuilt siunitBuilt : siUnitsBuilt) {
			Preservation preservation = getPreservation(siunitBuilt.type);
			preservation.built += 1;
		}
	}
}
