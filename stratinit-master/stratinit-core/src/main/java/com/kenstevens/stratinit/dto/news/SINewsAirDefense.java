package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.type.NewsCategory;
import com.kenstevens.stratinit.type.UnitType;


public class SINewsAirDefense extends SINewsOpponents implements SINewsCountable {
	private static final long serialVersionUID = 1L;
	public int count;
	public final UnitType nationUnitType;

	public SINewsAirDefense(Unit attackerUnit, Nation opponent, int count) {
		super(attackerUnit.getNation(), opponent, NewsCategory.AIR_DEFENCE);
		this.count = count;
		this.nationUnitType = attackerUnit.getType();
	}

	public SINewsAirDefense(String nationName, UnitType nationUnitType, String opponentName, int count) {
		super(nationName, opponentName, NewsCategory.AIR_DEFENCE);
		this.count = count;
		this.nationUnitType = nationUnitType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((nationUnitType == null) ? 0 : nationUnitType.hashCode());
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
		SINewsAirDefense other = (SINewsAirDefense) obj;
		if (nationUnitType == null) {
			return other.nationUnitType == null;
		} else return nationUnitType.equals(other.nationUnitType);
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
