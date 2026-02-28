package com.kenstevens.stratinit.server.bot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenstevens.stratinit.type.BotPersonality;

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
    public double zeppelinWaterBonus = 0.3;
    public double engineerSwimDesire = 1.2;
    public double engineerIslandHopBoost = 1.5;
    public double zeppelinIslandHopBonus = 0.5;

    // Production weights
    public double tankDesire = 0.6;

    @JsonIgnore
    public transient BotPersonality personality;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final java.util.Map<BotPersonality, BotWeights> PERSONALITY_CACHE = new java.util.EnumMap<>(BotPersonality.class);

    static {
        for (BotPersonality p : BotPersonality.values()) {
            PERSONALITY_CACHE.put(p, createForPersonality(p));
        }
    }

    public static BotWeights forPersonality(BotPersonality personality) {
        return PERSONALITY_CACHE.get(personality);
    }

    private static BotWeights createForPersonality(BotPersonality personality) {
        BotWeights w = new BotWeights();
        w.personality = personality;
        switch (personality) {
            case TECH -> {
                w.defenseBaseWeight = 1.5;
                w.cityDefenseDesire = 1.5;
                w.satelliteLaunchDesire = 2.0;
                w.icbmLaunchDesire = 3.0;
                w.navalBaseWeight = 0.0;
                w.navalCombatDesire = 0.0;
                w.transportLoadDesire = 0.0;
                w.transportDestinationDesire = 0.0;
                w.disembarkDesire = 0.0;
                w.expansionBaseWeight = 0.1;
                w.militaryBaseWeight = 0.2;
                w.attackWeakDesire = 0.2;
                w.engineerSwimDesire = 0.0;
                w.engineerIslandHopBoost = 0.0;
                w.techCentreDesire = 2.0;
                w.researchGuaranteeMultiplier = 5.0;
            }
            case RUSH -> {
                w.transportLoadDesire = 2.0;
                w.transportDestinationDesire = 2.0;
                w.disembarkDesire = 2.0;
                w.navalBaseWeight = 1.5;
                w.navalCombatDesire = 1.2;
                w.navalEscortBonus = 1.0;
                w.militaryBaseWeight = 1.5;
                w.attackWeakDesire = 1.5;
                w.coordinationBonus = 0.8;
                w.expansionBaseWeight = 0.3;
                w.techCentreDesire = 0.2;
                w.researchGuaranteeMultiplier = 1.0;
                w.engineerDesire = 0.2;
                w.engineerGuaranteeMultiplier = 1.0;
                w.buildCityDesire = 0.2;
                w.coastalCityDesire = 2.0;
            }
            case BOOM -> {
                w.expansionBaseWeight = 2.0;
                w.buildCityDesire = 2.0;
                w.engineerDesire = 2.0;
                w.engineerGuaranteeMultiplier = 5.0;
                w.engineerSwimDesire = 2.0;
                w.engineerIslandHopBoost = 3.0;
                w.zeppelinScoutDesire = 2.0;
                w.zeppelinWaterBonus = 1.0;
                w.zeppelinIslandHopBonus = 1.5;
                w.homeIslandExplorationBonus = 1.5;
                w.earlyExpansionBonus = 1.5;
                w.militaryBaseWeight = 0.3;
                w.defenseBaseWeight = 0.3;
                w.navalBaseWeight = 0.5;
                w.techCentreDesire = 0.3;
            }
            case TURTLE -> {
                w.defenseBaseWeight = 2.0;
                w.cityDefenseDesire = 2.0;
                w.undefendedPenalty = 2.0;
                w.techCentreDesire = 1.5;
                w.researchGuaranteeMultiplier = 5.0;
                w.navalBaseWeight = 0.0;
                w.navalCombatDesire = 0.0;
                w.transportLoadDesire = 0.0;
                w.transportDestinationDesire = 0.0;
                w.disembarkDesire = 0.0;
                w.expansionBaseWeight = 0.1;
                w.engineerSwimDesire = 0.0;
                w.engineerIslandHopBoost = 0.0;
                w.militaryBaseWeight = 0.8;
                w.airStrikeDesire = 1.2;
            }
        }
        return w;
    }

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
