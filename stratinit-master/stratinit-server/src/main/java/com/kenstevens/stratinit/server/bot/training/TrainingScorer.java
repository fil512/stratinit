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
    private static final double CITY_WEIGHT = 10.0;
    private static final double UNIT_WEIGHT = 1.0;
    private static final double TECH_WEIGHT = 0.5;
    private static final double EXPLORATION_WEIGHT = 0.1;

    @Autowired
    private CityDao cityDao;
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private SectorDao sectorDao;

    public double score(Nation nation) {
        return computeRawPower(nation);
    }

    public Map<String, Double> scoreAll(Map<String, Nation> nations) {
        // Compute raw power for each nation
        Map<String, Double> rawPowers = new LinkedHashMap<>();
        for (Map.Entry<String, Nation> entry : nations.entrySet()) {
            rawPowers.put(entry.getKey(), computeRawPower(entry.getValue()));
        }

        // Rank by raw power (highest = rank 1)
        List<Map.Entry<String, Double>> ranked = new ArrayList<>(rawPowers.entrySet());
        ranked.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        int n = ranked.size();
        Map<String, Double> scores = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            String name = ranked.get(i).getKey();
            double rawPower = ranked.get(i).getValue();
            int rank = i + 1;
            // Top-ranked gets full power, bottom gets power/N
            double score = rawPower * (n - rank + 1.0) / n;
            scores.put(name, score);
        }

        return scores;
    }

    private double computeRawPower(Nation nation) {
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
