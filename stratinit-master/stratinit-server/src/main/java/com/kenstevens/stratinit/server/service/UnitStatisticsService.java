package com.kenstevens.stratinit.server.service;

import com.kenstevens.stratinit.client.model.GameHistory;
import com.kenstevens.stratinit.client.model.UnitBase;
import com.kenstevens.stratinit.client.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.dto.SIUnitDayRow;
import com.kenstevens.stratinit.dto.SIUnitLove;
import com.kenstevens.stratinit.repo.GameHistoryRepo;
import com.kenstevens.stratinit.repo.UnitBuildAuditRepo;
import com.kenstevens.stratinit.type.UnitBaseType;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UnitStatisticsService {
    @Autowired
    private GameHistoryRepo gameHistoryRepo;
    @Autowired
    private UnitBuildAuditRepo unitBuildAuditRepo;

    public List<SIUnitLove> getGameUnitLove(int gameId) {
        List<GameHistory> games = gameHistoryRepo.findByGameId(gameId);
        GameHistory game = games.isEmpty() ? null : games.get(0);
        if (game == null) {
            return Collections.emptyList();
        }
        Map<UnitType, Integer> countMap = new EnumMap<>(UnitType.class);
        for (UnitType unitType : UnitBase.orderedUnitTypes()) {
            countMap.put(unitType, 0);
        }
        List<UnitBuildAudit> buildAudits = unitBuildAuditRepo.findByGameId(gameId);
        for (UnitBuildAudit audit : buildAudits) {
            countMap.merge(audit.getType(), 1, Integer::sum);
        }
        List<SIUnitLove> result = new ArrayList<>();
        for (UnitType unitType : UnitBase.orderedUnitTypes()) {
            int count = countMap.getOrDefault(unitType, 0);
            int love = calculateLove(unitType, count);
            result.add(new SIUnitLove(unitType.name(), love));
        }
        return result;
    }

    public List<SIUnitDayRow> getPlayerUnits(int gameId, UnitBaseType unitBaseType, String username) {
        List<GameHistory> games = gameHistoryRepo.findByGameId(gameId);
        GameHistory game = games.isEmpty() ? null : games.get(0);
        if (game == null) {
            return Collections.emptyList();
        }
        List<UnitBuildAudit> buildAudits = unitBuildAuditRepo.findByGameIdAndUsername(gameId, username);
        Date gameStart = game.getStartTime();
        List<UnitType> unitTypes = UnitBase.orderedUnitTypes(unitBaseType);

        // Count units by day and type
        Map<Integer, Map<UnitType, Integer>> dayTypeCounts = new HashMap<>();
        for (UnitBuildAudit audit : buildAudits) {
            if (audit.getDate() == null || gameStart == null) {
                continue;
            }
            int day = (int) ((audit.getDate().getTime() - gameStart.getTime()) / (24 * 3600 * 1000));
            if (day < 0 || day >= 10) {
                continue;
            }
            if (!unitTypes.contains(audit.getType())) {
                continue;
            }
            dayTypeCounts
                    .computeIfAbsent(day, k -> new EnumMap<>(UnitType.class))
                    .merge(audit.getType(), 1, Integer::sum);
        }

        List<SIUnitDayRow> result = new ArrayList<>();
        for (int day = 0; day < 10; day++) {
            Map<String, Integer> counts = new LinkedHashMap<>();
            Map<UnitType, Integer> typeCounts = dayTypeCounts.getOrDefault(day, Collections.emptyMap());
            for (UnitType unitType : unitTypes) {
                counts.put(unitType.name(), typeCounts.getOrDefault(unitType, 0));
            }
            result.add(new SIUnitDayRow(day + 1, counts));
        }
        return result;
    }

    public List<String> getGamePlayers(int gameId) {
        return unitBuildAuditRepo.findDistinctUsernamesByGameId(gameId);
    }

    private int calculateLove(UnitType unitType, int count) {
        UnitBase unitBase = UnitBase.getUnitBase(unitType);
        int topTech = UnitBase.getMaxTech() + 1;
        int tech = unitBase.getTech();
        int techFactor = topTech / (topTech - tech);
        int cost = unitBase.getProductionTime();
        return count * techFactor * cost;
    }
}
