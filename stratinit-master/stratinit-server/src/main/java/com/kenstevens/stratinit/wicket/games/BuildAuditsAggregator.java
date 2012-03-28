package com.kenstevens.stratinit.wicket.games;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import com.kenstevens.stratinit.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.type.UnitType;

public class BuildAuditsAggregator {
	private static final Comparator<UnitDay> BY_UNIT_BY_DAY = new Comparator<UnitDay>() {

		@Override
		public int compare(UnitDay unitDay1, UnitDay unitDay2) {
			if (unitDay1.day == unitDay2.day) {
				return unitDay1.unitType.compareTo(unitDay2.unitType);
			}
			return Integer.valueOf(unitDay1.day).compareTo(unitDay2.day);
		}
		
	};
	private final List<UnitBuildAudit> buildAudits;
	private final Multiset<UnitDay> unitDaySet = TreeMultiset.create(BY_UNIT_BY_DAY);

	public BuildAuditsAggregator(List<UnitBuildAudit> buildAudits) {
		this.buildAudits = buildAudits;
	}

	public List<PlayerUnitCount> getUnitsBuiltByDay() {
		if (buildAudits.isEmpty()) {
			return Lists.newArrayList();
		}
		Date gameStart = getGameStart();
		for (UnitBuildAudit buildAudit: buildAudits) {
			categorize(gameStart, buildAudit);
		}
		List<PlayerUnitCount> unitsBuilt = Lists.newArrayList();
		for (UnitDay unitDay : unitDaySet.elementSet()) {
			unitsBuilt.add(new PlayerUnitCount(unitDay.day, unitDay.unitType, unitDaySet.count(unitDay)));
		}
		return unitsBuilt;
	}

	private void categorize(Date gameStart, UnitBuildAudit buildAudit) {
		int day = getDay(gameStart, buildAudit);
		addRecord(day, buildAudit.getType());
	}

	private void addRecord(int day, UnitType type) {
		unitDaySet.add(new UnitDay(day, type));
	}

	private int getDay(Date gameStart, UnitBuildAudit buildAudit) {
		long timeDeltaMillis = buildAudit.getDate().getTime() - gameStart.getTime();
		long days = timeDeltaMillis / (24 * 3600 * 1000);
		return (int)days;
	}

	private Date getGameStart() {
		return buildAudits.get(0).getDate();
	}

}
