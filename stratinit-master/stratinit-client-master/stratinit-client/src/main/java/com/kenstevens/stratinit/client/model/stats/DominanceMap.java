package com.kenstevens.stratinit.client.model.stats;

import java.util.*;

public class DominanceMap {
	private final Map<String, Dominance> dominanceMap = new HashMap<String, Dominance>();

	protected List<String[]> getOpponentStats() {
		List<String[]> retval = new ArrayList<String[]>();
		Dominance total = new Dominance();
		for (String opponent : getOpponents()) {
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

	public String getWorstOpponent() {
		String worstOpponent = null;
		int worstTotal = -1;
		for (String opponent : getOpponents()) {
			Dominance dominance = getPlayerDominance(opponent);
			int totalValue = dominance.iKilledValue + dominance.iLostValue;
			if (totalValue > worstTotal) {
				worstTotal = totalValue;
				worstOpponent = opponent;
			}
		}
		return worstOpponent;
	}

}
