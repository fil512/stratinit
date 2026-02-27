package com.kenstevens.stratinit.server.bot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BotWeights {
    // Economy weights
    public double economyBaseWeight = 1.0;
    public double techCentreDesire = 0.8;
    public double infantryDesire = 0.7;
    public double engineerDesire = 0.8;
    public double engineerGuaranteeMultiplier = 3.0;

    // Expansion weights
    public double expansionBaseWeight = 0.9;
    public double distancePenalty = 0.1;
    public double buildCityDesire = 0.7;

    // Military weights
    public double militaryBaseWeight = 0.7;
    public double attackWeakDesire = 0.9;
    public double hpAdvantageFactor = 0.5;

    // Defense weights
    public double defenseBaseWeight = 0.8;
    public double cityDefenseDesire = 0.9;
    public double undefendedPenalty = 1.0;

    // Diplomacy weights
    public double diplomacyBaseWeight = 0.3;

    // Time-based modifiers
    public double earlyExpansionBonus = 0.5;
    public double lateMilitaryBonus = 0.3;

    // Naval weights
    public double navalBaseWeight = 0.6;
    public double navalCombatDesire = 0.7;
    public double transportLoadDesire = 0.5;

    // Air weights
    public double airStrikeDesire = 0.7;

    // Tech/strategic weights
    public double satelliteLaunchDesire = 0.5;
    public double icbmLaunchDesire = 0.8;

    // Transport destination planning
    public double transportDestinationDesire = 0.9;
    public double disembarkDesire = 1.0;

    // Neutral city capture
    public double neutralCityCaptureDesire = 1.2;

    // Coordination weights
    public double coordinationBonus = 0.3;
    public double airSupportBonus = 0.4;
    public double navalEscortBonus = 0.5;
    public double massAttackThreshold = 2.0;

    // Island expansion weights
    public double zeppelinScoutDesire = 1.0;
    public double homeIslandExplorationBonus = 0.5;
    public double researchGuaranteeMultiplier = 3.0;
    public double coastalCityDesire = 1.2;
    public double moveToCoastDesire = 0.6;

    // Production weights
    public double tankDesire = 0.6;

    private static final ObjectMapper mapper = new ObjectMapper();

    public String toJson() {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize BotWeights", e);
        }
    }

    public static BotWeights fromJson(String json) {
        try {
            return mapper.readValue(json, BotWeights.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize BotWeights", e);
        }
    }
}
