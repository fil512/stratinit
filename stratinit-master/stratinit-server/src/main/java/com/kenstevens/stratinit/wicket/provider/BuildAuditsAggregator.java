package com.kenstevens.stratinit.wicket.provider;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.type.UnitBaseType;
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
	private final Date gameStart;

	public BuildAuditsAggregator(Date gameStart, List<UnitBuildAudit> buildAudits) {
		this.gameStart = gameStart;
		this.buildAudits = buildAudits;
	}

	private void analyzeBuildAudits() {
		for (UnitBuildAudit buildAudit: buildAudits) {
			categorize(gameStart, buildAudit);
		}
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
		return (int)(timeDeltaMillis / (24 * 3600 * 1000));
	}

	public List<List<Object>> getFullUnitsBuiltByDay(UnitBaseType unitBaseType) {
		analyzeBuildAudits();
		List<List<Object>> retval = Lists.newArrayList();
		for (int day = 0; day < 10; ++day) {
			List<Object> list = Lists.newArrayList();
			list.add("'"+(day+1)+"'");
			for (UnitType unitType : UnitBase.orderedUnitTypes(unitBaseType)) {
				list.add(unitDaySet.count(new UnitDay(day, unitType)));
			}
			retval.add(list);
		}
		return retval;
	}
	
	public List<DayUnitsListRow> getDayUnitsListRows(UnitBaseType unitBaseType) {
		analyzeBuildAudits();
		List<DayUnitsListRow> retval = Lists.newArrayList();
		for (UnitType unitType : UnitBase.orderedUnitTypes(unitBaseType)) {
			List<Integer> list = Lists.newArrayList();
			for (int day = 0; day < 10; ++day) {
				list.add(unitDaySet.count(new UnitDay(day, unitType)));
			}
			DayUnitsListRow row = new DayUnitsListRow(unitType, list);
			retval.add(row);
		}
		return retval;
	}

}
