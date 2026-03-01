package com.kenstevens.stratinit.server.bot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenstevens.stratinit.type.BotPersonality;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PhasedBotWeights {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Map<BotPersonality, PhasedBotWeights> PERSONALITY_CACHE = new EnumMap<>(BotPersonality.class);

    public double[] thresholds = {3.0, 6.0, 10.0};
    public BotWeights[] phases = {new BotWeights(), new BotWeights(), new BotWeights(), new BotWeights()};

    static {
        for (BotPersonality p : BotPersonality.values()) {
            PERSONALITY_CACHE.put(p, createForPersonality(p));
        }
    }

    public static PhasedBotWeights forPersonality(BotPersonality personality) {
        return PERSONALITY_CACHE.get(personality);
    }

    /**
     * Resolve a single BotWeights by linearly interpolating between adjacent phases
     * based on current tech level.
     */
    public BotWeights resolve(double tech) {
        // Below first threshold → pure phase 0
        if (tech <= thresholds[0]) {
            return copyWeights(phases[0]);
        }
        // Above last threshold → pure last phase
        if (tech >= thresholds[thresholds.length - 1]) {
            return copyWeights(phases[phases.length - 1]);
        }
        // Find which two phases to interpolate between
        for (int i = 0; i < thresholds.length; i++) {
            if (tech < thresholds[i]) {
                double prevThreshold = (i == 0) ? 0.0 : thresholds[i - 1];
                double t = (tech - prevThreshold) / (thresholds[i] - prevThreshold);
                return lerpWeights(phases[i], phases[i + 1], t);
            }
        }
        return copyWeights(phases[phases.length - 1]);
    }

    /**
     * Wrap a single flat BotWeights into 4 identical phases.
     */
    public static PhasedBotWeights fromFlat(BotWeights flat) {
        PhasedBotWeights phased = new PhasedBotWeights();
        for (int i = 0; i < phased.phases.length; i++) {
            phased.phases[i] = copyWeights(flat);
        }
        return phased;
    }

    private static BotWeights lerpWeights(BotWeights a, BotWeights b, double t) {
        BotWeights result = new BotWeights();
        for (Field field : BotWeights.class.getDeclaredFields()) {
            if (field.getType() != double.class) continue;
            try {
                double va = field.getDouble(a);
                double vb = field.getDouble(b);
                field.setDouble(result, va + (vb - va) * t);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to interpolate field " + field.getName(), e);
            }
        }
        return result;
    }

    private static BotWeights copyWeights(BotWeights source) {
        BotWeights copy = new BotWeights();
        for (Field field : BotWeights.class.getDeclaredFields()) {
            if (field.getType() != double.class) continue;
            try {
                field.setDouble(copy, field.getDouble(source));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to copy field " + field.getName(), e);
            }
        }
        return copy;
    }

    public String toJson() {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize PhasedBotWeights", e);
        }
    }

    public static PhasedBotWeights fromJson(String json) {
        try {
            return mapper.readValue(json, PhasedBotWeights.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize PhasedBotWeights", e);
        }
    }

    private static PhasedBotWeights createForPersonality(BotPersonality personality) {
        PhasedBotWeights pw = new PhasedBotWeights();
        switch (personality) {
            case TECH -> {
                // EARLY: turtle up, research hard
                BotWeights early = new BotWeights();
                early.defenseBaseWeight = 1.5;
                early.cityDefenseDesire = 1.5;
                early.techCentreDesire = 2.0;
                early.researchGuaranteeMultiplier = 5.0;
                early.navalBaseWeight = 0.0;
                early.navalCombatDesire = 0.0;
                early.transportLoadDesire = 0.0;
                early.transportDestinationDesire = 0.0;
                early.disembarkDesire = 0.0;
                early.expansionBaseWeight = 0.1;
                early.militaryBaseWeight = 0.2;
                early.attackWeakDesire = 0.2;
                early.engineerSwimDesire = 0.0;
                early.engineerIslandHopBoost = 0.0;
                pw.phases[0] = early;

                // MID: still research-focused, slight expansion
                BotWeights mid = new BotWeights();
                mid.defenseBaseWeight = 1.5;
                mid.cityDefenseDesire = 1.5;
                mid.techCentreDesire = 2.0;
                mid.researchGuaranteeMultiplier = 5.0;
                mid.navalBaseWeight = 0.0;
                mid.navalCombatDesire = 0.0;
                mid.transportLoadDesire = 0.0;
                mid.transportDestinationDesire = 0.0;
                mid.disembarkDesire = 0.0;
                mid.expansionBaseWeight = 0.3;
                mid.militaryBaseWeight = 0.3;
                mid.attackWeakDesire = 0.3;
                mid.engineerSwimDesire = 0.0;
                mid.engineerIslandHopBoost = 0.0;
                pw.phases[1] = mid;

                // LATE: start using advanced units
                BotWeights late = new BotWeights();
                late.defenseBaseWeight = 1.2;
                late.cityDefenseDesire = 1.2;
                late.techCentreDesire = 1.5;
                late.researchGuaranteeMultiplier = 3.0;
                late.navalBaseWeight = 0.0;
                late.navalCombatDesire = 0.0;
                late.transportLoadDesire = 0.0;
                late.transportDestinationDesire = 0.0;
                late.disembarkDesire = 0.0;
                late.expansionBaseWeight = 0.1;
                late.militaryBaseWeight = 0.8;
                late.attackWeakDesire = 0.8;
                late.airStrikeDesire = 1.5;
                late.engineerSwimDesire = 0.0;
                late.engineerIslandHopBoost = 0.0;
                pw.phases[2] = late;

                // ENDGAME: ICBMs and satellites
                BotWeights endgame = new BotWeights();
                endgame.defenseBaseWeight = 1.0;
                endgame.cityDefenseDesire = 1.0;
                endgame.techCentreDesire = 1.0;
                endgame.researchGuaranteeMultiplier = 2.0;
                endgame.satelliteLaunchDesire = 2.0;
                endgame.icbmLaunchDesire = 3.0;
                endgame.navalBaseWeight = 0.0;
                endgame.navalCombatDesire = 0.0;
                endgame.transportLoadDesire = 0.0;
                endgame.transportDestinationDesire = 0.0;
                endgame.disembarkDesire = 0.0;
                endgame.expansionBaseWeight = 0.1;
                endgame.militaryBaseWeight = 1.0;
                endgame.attackWeakDesire = 1.0;
                endgame.airStrikeDesire = 1.5;
                endgame.engineerSwimDesire = 0.0;
                endgame.engineerIslandHopBoost = 0.0;
                pw.phases[3] = endgame;
            }
            case RUSH -> {
                // EARLY: fast transport rush
                BotWeights early = new BotWeights();
                early.transportLoadDesire = 2.0;
                early.transportDestinationDesire = 2.0;
                early.disembarkDesire = 2.0;
                early.navalBaseWeight = 1.5;
                early.navalCombatDesire = 0.5;
                early.navalEscortBonus = 1.0;
                early.militaryBaseWeight = 1.0;
                early.attackWeakDesire = 1.5;
                early.coordinationBonus = 0.8;
                early.expansionBaseWeight = 0.5;
                early.coastalCityDesire = 2.0;
                early.techCentreDesire = 0.2;
                early.researchGuaranteeMultiplier = 1.0;
                early.engineerDesire = 0.2;
                early.engineerGuaranteeMultiplier = 1.0;
                early.buildCityDesire = 0.2;
                pw.phases[0] = early;

                // MID: naval escort, continued aggression
                BotWeights mid = new BotWeights();
                mid.transportLoadDesire = 2.0;
                mid.transportDestinationDesire = 2.0;
                mid.disembarkDesire = 2.0;
                mid.navalBaseWeight = 1.5;
                mid.navalCombatDesire = 1.5;
                mid.navalEscortBonus = 1.5;
                mid.militaryBaseWeight = 1.5;
                mid.attackWeakDesire = 1.5;
                mid.coordinationBonus = 1.0;
                mid.expansionBaseWeight = 0.3;
                mid.coastalCityDesire = 1.5;
                mid.techCentreDesire = 0.2;
                mid.researchGuaranteeMultiplier = 1.0;
                mid.engineerDesire = 0.2;
                mid.engineerGuaranteeMultiplier = 1.0;
                mid.buildCityDesire = 0.2;
                pw.phases[1] = mid;

                // LATE: infantry waves with tank support
                BotWeights late = new BotWeights();
                late.transportLoadDesire = 1.5;
                late.transportDestinationDesire = 1.5;
                late.disembarkDesire = 1.5;
                late.navalBaseWeight = 1.2;
                late.navalCombatDesire = 1.2;
                late.navalEscortBonus = 1.0;
                late.militaryBaseWeight = 2.0;
                late.attackWeakDesire = 2.0;
                late.coordinationBonus = 1.2;
                late.tankDesire = 1.5;
                late.expansionBaseWeight = 0.2;
                late.coastalCityDesire = 1.0;
                late.techCentreDesire = 0.2;
                late.researchGuaranteeMultiplier = 1.0;
                late.engineerDesire = 0.2;
                late.engineerGuaranteeMultiplier = 1.0;
                late.buildCityDesire = 0.2;
                pw.phases[2] = late;

                // ENDGAME: all-out push
                BotWeights endgame = new BotWeights();
                endgame.transportLoadDesire = 1.5;
                endgame.transportDestinationDesire = 1.5;
                endgame.disembarkDesire = 1.5;
                endgame.navalBaseWeight = 1.2;
                endgame.navalCombatDesire = 1.2;
                endgame.navalEscortBonus = 1.0;
                endgame.militaryBaseWeight = 2.5;
                endgame.attackWeakDesire = 2.5;
                endgame.coordinationBonus = 1.5;
                endgame.tankDesire = 2.0;
                endgame.airStrikeDesire = 1.5;
                endgame.expansionBaseWeight = 0.1;
                endgame.coastalCityDesire = 0.5;
                endgame.techCentreDesire = 0.2;
                endgame.researchGuaranteeMultiplier = 1.0;
                endgame.engineerDesire = 0.1;
                endgame.engineerGuaranteeMultiplier = 1.0;
                endgame.buildCityDesire = 0.1;
                pw.phases[3] = endgame;
            }
            case BOOM -> {
                // EARLY: all-in engineers and expansion
                BotWeights early = new BotWeights();
                early.expansionBaseWeight = 2.0;
                early.buildCityDesire = 2.0;
                early.engineerDesire = 2.0;
                early.engineerGuaranteeMultiplier = 5.0;
                early.engineerSwimDesire = 2.0;
                early.engineerIslandHopBoost = 3.0;
                early.zeppelinScoutDesire = 2.0;
                early.zeppelinWaterBonus = 1.0;
                early.zeppelinIslandHopBonus = 1.5;
                early.homeIslandExplorationBonus = 1.5;
                early.militaryBaseWeight = 0.2;
                early.defenseBaseWeight = 0.3;
                early.navalBaseWeight = 0.5;
                early.techCentreDesire = 0.3;
                pw.phases[0] = early;

                // MID: transition, moderate expansion + production advantage
                BotWeights mid = new BotWeights();
                mid.expansionBaseWeight = 1.5;
                mid.buildCityDesire = 1.5;
                mid.engineerDesire = 1.5;
                mid.engineerGuaranteeMultiplier = 3.0;
                mid.engineerSwimDesire = 1.5;
                mid.engineerIslandHopBoost = 2.0;
                mid.zeppelinScoutDesire = 1.5;
                mid.zeppelinWaterBonus = 0.5;
                mid.zeppelinIslandHopBonus = 1.0;
                mid.homeIslandExplorationBonus = 1.0;
                mid.militaryBaseWeight = 0.5;
                mid.defenseBaseWeight = 0.5;
                mid.navalBaseWeight = 0.8;
                mid.navalCombatDesire = 0.8;
                mid.techCentreDesire = 0.5;
                pw.phases[1] = mid;

                // LATE: leverage production advantage militarily
                BotWeights late = new BotWeights();
                late.expansionBaseWeight = 0.8;
                late.buildCityDesire = 0.8;
                late.engineerDesire = 0.8;
                late.engineerGuaranteeMultiplier = 2.0;
                late.engineerSwimDesire = 0.5;
                late.engineerIslandHopBoost = 1.0;
                late.zeppelinScoutDesire = 0.8;
                late.militaryBaseWeight = 1.2;
                late.defenseBaseWeight = 0.6;
                late.navalBaseWeight = 0.8;
                late.navalCombatDesire = 1.0;
                late.tankDesire = 1.5;
                late.airStrikeDesire = 1.0;
                late.techCentreDesire = 0.5;
                pw.phases[2] = late;

                // ENDGAME: full military from large production base
                BotWeights endgame = new BotWeights();
                endgame.expansionBaseWeight = 0.3;
                endgame.buildCityDesire = 0.3;
                endgame.engineerDesire = 0.3;
                endgame.engineerGuaranteeMultiplier = 1.0;
                endgame.engineerSwimDesire = 0.0;
                endgame.engineerIslandHopBoost = 0.0;
                endgame.militaryBaseWeight = 1.8;
                endgame.defenseBaseWeight = 0.6;
                endgame.navalBaseWeight = 1.0;
                endgame.navalCombatDesire = 1.2;
                endgame.tankDesire = 2.0;
                endgame.airStrikeDesire = 1.5;
                endgame.techCentreDesire = 0.3;
                pw.phases[3] = endgame;
            }
            case TURTLE -> {
                // EARLY: high defense, tech focus
                BotWeights early = new BotWeights();
                early.defenseBaseWeight = 2.0;
                early.cityDefenseDesire = 2.0;
                early.undefendedPenalty = 2.0;
                early.techCentreDesire = 1.5;
                early.researchGuaranteeMultiplier = 5.0;
                early.navalBaseWeight = 0.0;
                early.navalCombatDesire = 0.0;
                early.transportLoadDesire = 0.0;
                early.transportDestinationDesire = 0.0;
                early.disembarkDesire = 0.0;
                early.expansionBaseWeight = 0.1;
                early.engineerSwimDesire = 0.0;
                early.engineerIslandHopBoost = 0.0;
                early.militaryBaseWeight = 0.5;
                pw.phases[0] = early;

                // MID: maintain defense, build navy for protection
                BotWeights mid = new BotWeights();
                mid.defenseBaseWeight = 2.0;
                mid.cityDefenseDesire = 2.0;
                mid.undefendedPenalty = 2.0;
                mid.techCentreDesire = 1.5;
                mid.researchGuaranteeMultiplier = 4.0;
                mid.navalBaseWeight = 0.0;
                mid.navalCombatDesire = 0.0;
                mid.transportLoadDesire = 0.0;
                mid.transportDestinationDesire = 0.0;
                mid.disembarkDesire = 0.0;
                mid.expansionBaseWeight = 0.1;
                mid.engineerSwimDesire = 0.0;
                mid.engineerIslandHopBoost = 0.0;
                mid.militaryBaseWeight = 0.8;
                mid.airStrikeDesire = 1.0;
                pw.phases[1] = mid;

                // LATE: air strikes with good tech
                BotWeights late = new BotWeights();
                late.defenseBaseWeight = 1.8;
                late.cityDefenseDesire = 1.8;
                late.undefendedPenalty = 1.5;
                late.techCentreDesire = 1.2;
                late.researchGuaranteeMultiplier = 3.0;
                late.navalBaseWeight = 0.0;
                late.navalCombatDesire = 0.0;
                late.transportLoadDesire = 0.0;
                late.transportDestinationDesire = 0.0;
                late.disembarkDesire = 0.0;
                late.expansionBaseWeight = 0.1;
                late.engineerSwimDesire = 0.0;
                late.engineerIslandHopBoost = 0.0;
                late.militaryBaseWeight = 1.2;
                late.airStrikeDesire = 1.5;
                late.tankDesire = 1.0;
                pw.phases[2] = late;

                // ENDGAME: ICBMs from safe position
                BotWeights endgame = new BotWeights();
                endgame.defenseBaseWeight = 1.5;
                endgame.cityDefenseDesire = 1.5;
                endgame.undefendedPenalty = 1.0;
                endgame.techCentreDesire = 1.0;
                endgame.researchGuaranteeMultiplier = 2.0;
                endgame.satelliteLaunchDesire = 1.5;
                endgame.icbmLaunchDesire = 2.5;
                endgame.navalBaseWeight = 0.0;
                endgame.navalCombatDesire = 0.0;
                endgame.transportLoadDesire = 0.0;
                endgame.transportDestinationDesire = 0.0;
                endgame.disembarkDesire = 0.0;
                endgame.expansionBaseWeight = 0.1;
                endgame.engineerSwimDesire = 0.0;
                endgame.engineerIslandHopBoost = 0.0;
                endgame.militaryBaseWeight = 1.5;
                endgame.airStrikeDesire = 1.5;
                endgame.tankDesire = 1.2;
                pw.phases[3] = endgame;
            }
        }
        return pw;
    }
}
