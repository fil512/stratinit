package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.type.NewsCategory;
import com.kenstevens.stratinit.type.UnitType;

public class SINewsNuclearDetonations extends SINewsOpponents implements SINewsCountable {
	private static final long serialVersionUID = 1L;
	public int count;
	public final UnitType launchableUnit;

	public SINewsNuclearDetonations(Unit nuke, Nation opponent, int count) {
		super(nuke.getNation(), opponent, NewsCategory.NUCLEAR_DETONATIONS);
		this.count = count;
		this.launchableUnit = nuke.getType();
	}

	public SINewsNuclearDetonations(String nationName, UnitType launchableUnit, String opponentName, int count) {
		super(nationName, opponentName, NewsCategory.NUCLEAR_DETONATIONS);
		this.count = count;
		this.launchableUnit = launchableUnit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((launchableUnit == null) ? 0 : launchableUnit.hashCode());
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
		SINewsNuclearDetonations other = (SINewsNuclearDetonations) obj;
		if (launchableUnit == null) {
			return other.launchableUnit == null;
		} else return launchableUnit.equals(other.launchableUnit);
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
