package com.kenstevens.stratinit.model.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DominanceMap {
	private final Map<String, Dominance> dominanceMap = new HashMap<String, Dominance>();

	protected List<String[]> getOpponentStats() {
		List<String[]> retval = new ArrayList<String[]>();
		Dominance total = new Dominance();
		for (String opponent : dominanceMap.keySet()) {
			Dominance dominance = dominanceMap.get(opponent);
			total.add(dominance);
			String[] line = new String[] { opponent, "" + dominance.iLostValue,
					"" + dominance.iKilledValue, "" + dominance.getNetValue(),
					"" + dominance.getDominance() +"%"};
			retval.add(line);
		}
		String[] line = new String[] { "TOTAL", "" + total.iLostValue,
				"" + total.iKilledValue, "" + total.getNetValue(),
				"" + total.getDominance()+"%" };
		retval.add(line);
		return retval;
	}

	protected Set<String> getOpponents() {
		return dominanceMap.keySet();
	}

	protected void clear() {
		dominanceMap.clear();
	}
	
	protected Dominance getPlayerDominance(String opponent) {
		Dominance dominance = dominanceMap.get(opponent);
		if (dominance == null) {
			dominance = new Dominance();
			dominanceMap.put(opponent, dominance);
		}
		return dominance;
	}

}
