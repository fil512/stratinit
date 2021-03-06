package com.kenstevens.stratinit.wicket.provider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kenstevens.stratinit.client.model.GameHistory;
import com.kenstevens.stratinit.client.model.UnitBase;
import com.kenstevens.stratinit.client.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.repo.GameHistoryRepo;
import com.kenstevens.stratinit.repo.UnitBuildAuditRepo;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UnitsBuiltProvider {
	@Autowired
	GameHistoryRepo gameHistoryRepo;
	@Autowired
	UnitBuildAuditRepo unitBuildAuditRepo;

	Map<String, Integer> buildCount = Maps.newHashMap();

	public List<GameUnitsBuilt> getUnitsBuilt() {
		List<GameUnitsBuilt> retval = Lists.newArrayList();
		List<GameHistory> games = gameHistoryRepo.findAll();
		Collections.reverse(games);
		for (GameHistory game : games) {
			GameUnitsBuilt gameUnitsBuilt = new GameUnitsBuilt(game);
			Map<UnitType, UnitsBuilt> unitsBuiltMap = getGameUnitsMap(game);
			gameUnitsBuilt.addAll(sort(unitsBuiltMap));
			retval.add(gameUnitsBuilt);
		}
		return retval;
	}

	private static final Comparator<UnitsBuilt> byLove = new Comparator<UnitsBuilt>() {

		@Override
		public int compare(UnitsBuilt o1, UnitsBuilt o2) {
			return Integer.valueOf(o2.getLove()).compareTo(o1.getLove());
		}

	};

	private Collection<? extends UnitsBuilt> sort(
			Map<UnitType, UnitsBuilt> unitsBuiltMap) {

		ArrayList<UnitsBuilt> retval = Lists.newArrayList(unitsBuiltMap
				.values());
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

	public List<List<Object>> getUnitLove(int gameId) {
		List<List<Object>> retval = Lists.newArrayList();
		GameHistory game = gameHistoryRepo.findByGameId(gameId);
		Map<UnitType, UnitsBuilt> unitsBuiltMap = getGameUnitsMap(game);
		for (UnitType unitType : UnitBase.orderedUnitTypes()) {
			List<Object> row = Lists.newArrayList();
			row.add(JavaScriptHelper.unitTypeAsJSString(unitType));
			row.add(unitsBuiltMap.get(unitType).getLove());
			retval.add(row);
		}
		return retval;
	}

	private Map<UnitType, UnitsBuilt> getGameUnitsMap(GameHistory game) {
		Map<UnitType, UnitsBuilt> unitsBuiltMap = newMap(game.getGameId());
		List<UnitBuildAudit> buildAudits = unitBuildAuditRepo.findByGameId(game.getGameId());
		for (UnitBuildAudit buildAudit : buildAudits) {
			UnitsBuilt unitsBuilt = unitsBuiltMap.get(buildAudit.getType());
			unitsBuilt.increment();
		}
		return unitsBuiltMap;
	}
}
