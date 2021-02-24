package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.type.NewsCategory;
import com.kenstevens.stratinit.type.UnitType;

public class SINewsFromTheFront extends SINewsOpponents implements SINewsCountable {
	private static final long serialVersionUID = 1L;
	public int count;
	public final boolean killed;
	public final UnitType nationUnitType;
	public final UnitType opponentUnitType;

	public SINewsFromTheFront(Unit nationUnit, Unit opponentUnit, boolean killed,
							  int count) {
		super(nationUnit.getNation(), opponentUnit.getNation(), NewsCategory.NEWS_FROM_THE_FRONT);
		this.count = count;
		this.killed = killed;
		this.nationUnitType = nationUnit.getType();
		this.opponentUnitType = opponentUnit.getType();
	}

	public SINewsFromTheFront(String nationName, UnitType nationUnitType, String opponentName, UnitType opponentUnitType, boolean killed, int count) {
		super(nationName, opponentName, NewsCategory.NEWS_FROM_THE_FRONT);
		this.count = count;
		this.killed = killed;
		this.nationUnitType = nationUnitType;
		this.opponentUnitType = opponentUnitType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (killed ? 1231 : 1237);
		result = prime * result
				+ ((nationUnitType == null) ? 0 : nationUnitType.hashCode());
		result = prime
				* result
				+ ((opponentUnitType == null) ? 0 : opponentUnitType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return typedEquals((SINewsFromTheFront) obj);
	}

	private boolean typedEquals(SINewsFromTheFront other) {
		if (killed != other.killed)
			return false;
		if (nationUnitType == null) {
			if (other.nationUnitType != null)
				return false;
		} else if (!nationUnitType.equals(other.nationUnitType))
			return false;
		if (opponentUnitType == null) {
			return other.opponentUnitType == null;
		} else return opponentUnitType.equals(other.opponentUnitType);
	}

	@Override
	public void increment(int count) {
		this.count += count;
	}

	@Override
	public int getCount() {
		return count;
	}
}
