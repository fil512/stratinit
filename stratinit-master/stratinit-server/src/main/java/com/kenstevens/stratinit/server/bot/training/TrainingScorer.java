package com.kenstevens.stratinit.server.bot.training;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TrainingScorer {
    @Autowired
    private CityDao cityDao;
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private SectorDao sectorDao;

    public double score(Nation nation) {
        return computeRawPower(nation);
    }

    /**
     * Score all nations using milestone-based scoring with z-score normalization.
     * Returns z-score normalized scores (mean=0, stddev=1) so map layout bias cancels out.
     */
    public Map<String, Double> scoreAll(Map<String, Nation> nations, TrainingActionLog actionLog) {
        Map<String, Double> rawScores = new LinkedHashMap<>();
        for (Map.Entry<String, Nation> entry : nations.entrySet()) {
            rawScores.put(entry.getKey(), computeMilestoneScore(entry.getValue(), entry.getKey(), actionLog));
        }
        return zScoreNormalize(rawScores);
    }

    /**
     * Legacy scoring without milestones (for backward compatibility).
     */
    public Map<String, Double> scoreAll(Map<String, Nation> nations) {
        Map<String, Double> rawScores = new LinkedHashMap<>();
        for (Map.Entry<String, Nation> entry : nations.entrySet()) {
            rawScores.put(entry.getKey(), computeRawPower(entry.getValue()));
        }
        return zScoreNormalize(rawScores);
    }

    private double computeMilestoneScore(Nation nation, String playerName, TrainingActionLog actionLog) {
        int cities = cityDao.getNumberOfCities(nation);
        List<Unit> units = unitDao.getUnits(nation);
        long aliveUnits = units.stream().filter(Unit::isAlive).count();
        double tech = nation.getTech();

        double score = 0;
        score += 5.0 * cities;
        score += 0.5 * aliveUnits;
        score += 0.3 * tech;

        // Milestone bonuses
        if (actionLog.hasExpandedOffHomeIsland(playerName)) {
            score += 15.0;
        }
        score += 5.0 * Math.min(actionLog.getCitiesOnNonHomeIslands(playerName), 3);
        if (actionLog.hasLoadedTransport(playerName)) {
            score += 5.0;
        }
        score += 5.0 * Math.min(actionLog.getEnemyUnitsKilled(playerName), 10);

        // Tempo bonuses — reward doing things earlier
        int firstCaptureTurn = actionLog.getFirstCityCapturedTurn(playerName);
        if (firstCaptureTurn < Integer.MAX_VALUE) {
            score += Math.max(0, 10.0 - (firstCaptureTurn / 100.0));
        }
        int firstTransportTurn = actionLog.getFirstTransportLoadedTurn(playerName);
        if (firstTransportTurn < Integer.MAX_VALUE) {
            score += Math.max(0, 5.0 - (firstTransportTurn / 300.0));
        }

        return score;
    }

    private double computeRawPower(Nation nation) {
        int cities = cityDao.getNumberOfCities(nation);
        List<Unit> units = unitDao.getUnits(nation);
        long aliveUnits = units.stream().filter(Unit::isAlive).count();
        double tech = nation.getTech();
        int explored = sectorDao.getSectorsSeen(nation).size();

        return 10.0 * cities + 1.0 * aliveUnits + 0.5 * tech + 0.1 * explored;
    }

    /**
     * Z-score normalize scores: (x - mean) / stddev.
     * If stddev is 0 (all scores equal), returns all zeros.
     */
    static Map<String, Double> zScoreNormalize(Map<String, Double> raw) {
        double sum = 0;
        for (double v : raw.values()) sum += v;
        double mean = sum / raw.size();

        double variance = 0;
        for (double v : raw.values()) variance += (v - mean) * (v - mean);
        double stddev = Math.sqrt(variance / raw.size());

        Map<String, Double> normalized = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : raw.entrySet()) {
            if (stddev > 0) {
                normalized.put(entry.getKey(), (entry.getValue() - mean) / stddev);
            } else {
                normalized.put(entry.getKey(), 0.0);
            }
        }
        return normalized;
    }

    public int getCitiesOwned(Nation nation) {
        return cityDao.getNumberOfCities(nation);
    }
}
