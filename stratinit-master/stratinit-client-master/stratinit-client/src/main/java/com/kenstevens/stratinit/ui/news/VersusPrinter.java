package com.kenstevens.stratinit.ui.news;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.swt.custom.StyledText;

import com.kenstevens.stratinit.dto.news.SINewsAirDefense;
import com.kenstevens.stratinit.dto.news.SINewsFromTheFront;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.dto.news.SINewsNuclearDetonations;
import com.kenstevens.stratinit.dto.news.SINewsOpponentConquest;
import com.kenstevens.stratinit.dto.news.SINewsOpponents;

public class VersusPrinter extends NewsPrinter {

	private class NationPair implements Comparable<NationPair>{
		private final String nation;
		private final String opponent;

		public NationPair(String nation, String opponent) {
			this.nation = nation;
			this.opponent = opponent;
		}

		public NationPair reverse() {
			return new NationPair(opponent, nation);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((nation == null) ? 0 : nation.hashCode());
			result = prime * result
					+ ((opponent == null) ? 0 : opponent.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NationPair other = (NationPair) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (nation == null) {
				if (other.nation != null)
					return false;
			} else if (!nation.equals(other.nation))
				return false;
			if (opponent == null) {
				if (other.opponent != null)
					return false;
			} else if (!opponent.equals(other.opponent))
				return false;
			return true;
		}

		@Override
		public int compareTo(NationPair other) {
			if (nation.equals(other.nation)) {
				return opponent.compareTo(other.opponent);
			}
			return nation.compareTo(other.nation);
		}

		private VersusPrinter getOuterType() {
			return VersusPrinter.this;
		}
	}

	public VersusPrinter(StyledText styledText) {
		super(styledText);
	}



	public void print(SINewsLogsDay siNewsDay) {
		SortedMap<NationPair, SINewsLogsDay> pairMap = new TreeMap<NationPair, SINewsLogsDay>();

		splitByPair(siNewsDay, pairMap);

		for (NationPair key : pairMap.keySet()) {
			printPair(key, pairMap.get(key));
		}
	}

	private void printPair(NationPair key, SINewsLogsDay siNewsDay) {
		printSubtitle(key.nation+" vs. "+key.opponent);
		new NuclearDetonationsPrinter(styledText).print(siNewsDay.getNuclearDetonations());
		new OpponentConquestPrinter(styledText).print(siNewsDay.getOpponentConquest());
		new NewsFromTheFrontPrinter(styledText).print(siNewsDay.getNewsFromTheFront());
		new AirDefensePrinter(styledText).print(siNewsDay.getAirDefense());
	}

	private void splitByPair(SINewsLogsDay siNewsDay,
			Map<NationPair, SINewsLogsDay> pairMap) {
		for (SINewsFromTheFront item : siNewsDay.getNewsFromTheFront()) {
			SINewsLogsDay siNewsLogsDay = createEntry(pairMap, item);
			siNewsLogsDay.addNewsFromTheFront(item);
		}

		for (SINewsAirDefense item : siNewsDay.getAirDefense()) {
			SINewsLogsDay siNewsLogsDay = createEntry(pairMap, item);
			siNewsLogsDay.addAirDefense(item);
		}

		for (SINewsNuclearDetonations item : siNewsDay.getNuclearDetonations()) {
			SINewsLogsDay siNewsLogsDay = createEntry(pairMap, item);
			siNewsLogsDay.addNuclearDetonations(item);
		}

		for (SINewsOpponentConquest item : siNewsDay.getOpponentConquest()) {
			SINewsLogsDay siNewsLogsDay = createEntry(pairMap, item);
			siNewsLogsDay.addOpponentConquest(item);
		}
	}

	private SINewsLogsDay createEntry(
			Map<NationPair, SINewsLogsDay> pairMap, SINewsOpponents item) {
		NationPair key = new NationPair(item.nationName, item.opponentName);
		SINewsLogsDay siNewsLogsDay = pairMap.get(key);
		if (siNewsLogsDay == null) {
			siNewsLogsDay = pairMap.get(key.reverse());
		}
		if (siNewsLogsDay == null) {
			siNewsLogsDay = new SINewsLogsDay();
			pairMap.put(key, siNewsLogsDay);
		}
		return siNewsLogsDay;
	}
}
