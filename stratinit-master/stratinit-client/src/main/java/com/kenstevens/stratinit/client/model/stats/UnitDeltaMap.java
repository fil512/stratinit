package com.kenstevens.stratinit.client.model.stats;

import com.kenstevens.stratinit.type.UnitType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnitDeltaMap {
	private final Map<String, Map<UnitType, Dominance>> unitDeltaMap = new HashMap<String, Map<UnitType, Dominance>>();

	protected void clear() {
		unitDeltaMap.clear();
	}


	protected Dominance getUnitDominance(String opponent, UnitType unitType) {
		Map<UnitType, Dominance> unitMap  = unitDeltaMap.get(opponent);
		if (unitMap == null) {
			unitMap = new HashMap<UnitType, Dominance>();
			unitDeltaMap.put(opponent, unitMap);
		}
		Dominance dominance = unitMap.get(unitType);
		if (dominance == null) {
			dominance = new Dominance();
			unitMap.put(unitType, dominance);
		}
		return dominance;
	}
	

	protected List<String[]> getOpponentStats(String opponent) {
		List<String[]> retval = new ArrayList<String[]>();
		Map<UnitType, Dominance> unitMap = unitDeltaMap.get(opponent);
		Dominance total = new Dominance();
		int totalNet = 0;
		int totalCost = 0;
		for (UnitType unitType : unitMap.keySet()) {
			Dominance dominance = unitMap.get(unitType);
			total.add(dominance);
			totalNet += dominance.getNet();
			totalCost += dominance.getCost(unitType);
			String[] line = new String[] { unitType.toString().toLowerCase(), "" + dominance.iLost,
					"" + dominance.iKilled, "" + dominance.getNet(),
					"" + dominance.getCost(unitType) };
			retval.add(line);
		}
		String[] line = new String[] { "TOTAL", "" + total.iLost,
				"" + total.iKilled, "" + totalNet,
				"" + totalCost };
		retval.add(line);
		return retval;
	}
}
