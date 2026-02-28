package com.kenstevens.stratinit.server.bot.training;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TrainingScorer {
    private static final double CITY_WEIGHT = 10.0;
    private static final double UNIT_WEIGHT = 1.0;
    private static final double TECH_WEIGHT = 0.5;
    private static final double EXPLORATION_WEIGHT = 0.1;

    private static final double ATTACKED_ENEMY_MULTIPLIER = 2.0;
    private static final double FOUND_ENEMY_MULTIPLIER = 1.5;

    private static final Set<String> ATTACK_ACTION_TYPES = Set.of(
            "AttackEnemyAction", "AttackNavalAction", "AttackWithAirAction", "LaunchICBMAction");

    @Autowired
    private CityDao cityDao;
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private SectorDao sectorDao;

    public double score(Nation nation) {
        return computeBaseScore(nation);
    }

    public double score(Nation nation, TrainingActionLog actionLog) {
        double baseScore = computeBaseScore(nation);

        // Check for enemy interaction multipliers
        String nationName = nation.getName();
        Map<String, List<TrainingActionLog.TurnSnapshot>> nationTurns = actionLog.getNationTurns();
        List<TrainingActionLog.TurnSnapshot> turns = nationTurns.get(nationName);

        if (turns != null) {
            boolean attackedEnemy = false;
            for (TrainingActionLog.TurnSnapshot turn : turns) {
                for (String actionType : turn.getExecutedCounts().keySet()) {
                    if (ATTACK_ACTION_TYPES.contains(actionType) && turn.getExecutedCounts().get(actionType) > 0) {
                        attackedEnemy = true;
                        break;
                    }
                }
                if (attackedEnemy) break;
            }

            if (attackedEnemy) {
                return baseScore * ATTACKED_ENEMY_MULTIPLIER;
            }

            // Check if bot found enemy cities (scouted them)
            List<City> seenCities = cityDao.getSeenCities(nation);
            boolean foundEnemy = seenCities.stream()
                    .anyMatch(c -> !c.getNation().equals(nation));
            if (foundEnemy) {
                return baseScore * FOUND_ENEMY_MULTIPLIER;
            }
        }

        return baseScore;
    }

    private double computeBaseScore(Nation nation) {
        int cities = cityDao.getNumberOfCities(nation);
        List<Unit> units = unitDao.getUnits(nation);
        long aliveUnits = units.stream().filter(Unit::isAlive).count();
        double tech = nation.getTech();

        int explored = sectorDao.getSectorsSeen(nation).size();

        return CITY_WEIGHT * cities + UNIT_WEIGHT * aliveUnits + TECH_WEIGHT * tech + EXPLORATION_WEIGHT * explored;
    }

    public int getCitiesOwned(Nation nation) {
        return cityDao.getNumberOfCities(nation);
    }
}
