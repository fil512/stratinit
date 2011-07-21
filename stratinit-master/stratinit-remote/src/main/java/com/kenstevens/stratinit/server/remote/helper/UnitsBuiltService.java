package com.kenstevens.stratinit.server.remote.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kenstevens.stratinit.dal.GameHistoryDal;
import com.kenstevens.stratinit.dal.UnitDal;
import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.type.UnitType;

@Service
public class UnitsBuiltService {
	@Autowired
	GameHistoryDal gameHistoryDal;
	@Autowired
	UnitDal unitDal;
	Map<String, Integer> buildCount = Maps.newHashMap();

	public List<UnitsBuilt> getUnitsBuilt() {
		List<UnitsBuilt> retval = Lists.newArrayList();
		List<GameHistory> games = gameHistoryDal.getAllGameHistories();
		Collections.reverse(games);
		for (GameHistory game : games) {
			Map<UnitType, UnitsBuilt> unitsBuiltMap = newMap(game.getGameId());
			List<UnitBuildAudit> buildAudits = unitDal.getBuildAudits(game.getGameId());
			for (UnitBuildAudit buildAudit : buildAudits) {
				UnitsBuilt unitsBuilt = unitsBuiltMap.get(buildAudit.getType());
				unitsBuilt.increment();
			}
			retval.addAll(sort(unitsBuiltMap));
		}
		return retval;
	}

	private static Comparator<UnitsBuilt> byLove = new Comparator<UnitsBuilt>() {

		@Override
		public int compare(UnitsBuilt o1, UnitsBuilt o2) {
			return Integer.valueOf(o2.getLove()).compareTo(o1.getLove());
		}
		
	};
	
	private Collection<? extends UnitsBuilt> sort(
			Map<UnitType, UnitsBuilt> unitsBuiltMap) {

		ArrayList<UnitsBuilt> retval = Lists.newArrayList(unitsBuiltMap.values());
		Collections.sort(retval, byLove);
		return retval;
	}

	private Map<UnitType, UnitsBuilt> newMap(int gameId) {
		Map<UnitType, UnitsBuilt> map = Maps.newHashMap();
		for (UnitType unitType : UnitBase.orderedUnitTypes()) {
			UnitsBuilt unitsBuilt = new UnitsBuilt(gameId, unitType);
			map.put(unitType, unitsBuilt);
		}
		return map;
	}
}
