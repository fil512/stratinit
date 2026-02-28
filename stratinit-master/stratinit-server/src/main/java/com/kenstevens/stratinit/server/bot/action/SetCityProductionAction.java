package com.kenstevens.stratinit.server.bot.action;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.UnitBase;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;
import com.kenstevens.stratinit.server.bot.BotWeights;
import com.kenstevens.stratinit.server.bot.BotWorldState;
import com.kenstevens.stratinit.server.service.CityService;
import com.kenstevens.stratinit.type.BotPersonality;
import com.kenstevens.stratinit.type.UnitType;

public class SetCityProductionAction implements BotAction {
    private final City city;
    private final UnitType unitType;
    private final Nation nation;
    private final CityService cityService;

    public SetCityProductionAction(City city, UnitType unitType, Nation nation, CityService cityService) {
        this.city = city;
        this.unitType = unitType;
        this.nation = nation;
        this.cityService = cityService;
    }

    @Override
    public BotActionCategory getCategory() {
        return BotActionCategory.ECONOMY;
    }

    @Override
    public double computeUtility(BotWorldState state, BotWeights weights) {
        // If city has no build, we set BUILD (newly captured city).
        // If city has a build, we set NEXT_BUILD — skip if already queued.
        if (city.getBuild() != null) {
            if (city.getNextBuild() != null) {
                return 0;
            }
            // Don't queue the same thing that's already building
            if (city.getBuild() == unitType) {
                return 0;
            }
        }

        UnitBase unitBase = UnitBase.getUnitBase(unitType);
        // Can't build if tech too low
        if (unitBase.getTech() > state.getTech()) {
            return 0;
        }

        // Personality-specific production logic overrides weight-based scoring
        if (weights.personality != null) {
            return computePersonalityUtility(state, weights);
        }

        return computeDefaultUtility(state, weights, unitBase);
    }

    private double computePersonalityUtility(BotWorldState state, BotWeights weights) {
        int cityCount = state.getMyCities().size();
        double tech = state.getTech();

        return switch (weights.personality) {
            case TECH -> computeTechUtility(state, cityCount, tech);
            case RUSH -> computeRushUtility(state);
            case BOOM -> computeBoomUtility(state);
            case TURTLE -> computeTurtleUtility(state, cityCount);
        };
    }

    private double computeTechUtility(BotWorldState state, int cityCount, double tech) {
        // Phase 1 (< 4 cities): All cities build ENGINEER
        if (cityCount < 4) {
            return unitType == UnitType.ENGINEER ? 10.0 : 0;
        }
        // Phase 2 (>= 4 cities, tech < 16): All cities build RESEARCH
        if (tech < 16) {
            return unitType == UnitType.RESEARCH ? 10.0 : 0;
        }
        // Phase 3 (tech >= 16): Half SATELLITE, half ICBM_3
        if (unitType != UnitType.SATELLITE && unitType != UnitType.ICBM_3) {
            return 0;
        }
        long satCount = state.countCitiesBuildingType(UnitType.SATELLITE);
        long icbmCount = state.countCitiesBuildingType(UnitType.ICBM_3);
        if (unitType == UnitType.SATELLITE) {
            return satCount <= icbmCount ? 10.0 : 5.0;
        }
        return icbmCount <= satCount ? 10.0 : 5.0;
    }

    private double computeRushUtility(BotWorldState state) {
        if (state.isCoastalCity(city)) {
            // Coastal cities: TRANSPORT if infantry-to-transport ratio < 6, else INFANTRY
            long infantryCount = state.countAliveUnitsOfType(UnitType.INFANTRY);
            long transportCount = state.countAliveUnitsOfType(UnitType.TRANSPORT);
            long transportBuilding = state.countCitiesBuildingType(UnitType.TRANSPORT);
            long effectiveTransports = transportCount + transportBuilding;
            if (effectiveTransports == 0 || (infantryCount / Math.max(1, effectiveTransports)) >= 6) {
                return unitType == UnitType.TRANSPORT ? 10.0 : (unitType == UnitType.INFANTRY ? 5.0 : 0);
            }
            return unitType == UnitType.INFANTRY ? 10.0 : (unitType == UnitType.TRANSPORT ? 3.0 : 0);
        }
        // Non-coastal cities: always INFANTRY
        return unitType == UnitType.INFANTRY ? 10.0 : 0;
    }

    private double computeBoomUtility(BotWorldState state) {
        // TECH cities: build ENGINEER (always)
        if (city.getBuild() == UnitType.RESEARCH) {
            return unitType == UnitType.ENGINEER ? 10.0 : 0;
        }
        // Coastal cities: ZEPPELIN or PATROL (for scouting)
        if (state.isCoastalCity(city)) {
            if (unitType == UnitType.ZEPPELIN) return 10.0;
            if (unitType == UnitType.PATROL) return 8.0;
            if (unitType == UnitType.ENGINEER) return 6.0;
            return 0;
        }
        // Non-coastal cities: build ENGINEER
        return unitType == UnitType.ENGINEER ? 10.0 : 0;
    }

    private double computeTurtleUtility(BotWorldState state, int cityCount) {
        // Phase 1 (< 2 engineers ever built): build ENGINEER
        long engineerCount = state.countAliveUnitsOfType(UnitType.ENGINEER);
        long engineerBuilding = state.countCitiesBuildingType(UnitType.ENGINEER);
        if ((engineerCount + engineerBuilding) < 2) {
            return unitType == UnitType.ENGINEER ? 10.0 : 0;
        }
        // Phase 2: Alternate RESEARCH / INFANTRY / FIGHTER across cities
        long researchCount = state.countCitiesBuildingType(UnitType.RESEARCH);
        long infantryCount = state.countCitiesBuildingType(UnitType.INFANTRY);
        long fighterCount = state.countCitiesBuildingType(UnitType.FIGHTER);
        if (unitType == UnitType.RESEARCH && researchCount <= infantryCount && researchCount <= fighterCount) return 10.0;
        if (unitType == UnitType.INFANTRY && infantryCount <= researchCount && infantryCount <= fighterCount) return 9.0;
        if (unitType == UnitType.FIGHTER && fighterCount <= researchCount && fighterCount <= infantryCount) return 8.0;
        if (unitType == UnitType.RESEARCH) return 7.0;
        if (unitType == UnitType.INFANTRY) return 6.0;
        if (unitType == UnitType.FIGHTER) return 5.0;
        return 0;
    }

    private double computeDefaultUtility(BotWorldState state, BotWeights weights, UnitBase unitBase) {
        double utility = weights.economyBaseWeight;

        if (unitType == UnitType.RESEARCH) {
            if (!state.isAnyResearchCity()) {
                utility = weights.economyBaseWeight * weights.researchGuaranteeMultiplier;
            } else {
                utility *= weights.techCentreDesire;
            }
        } else if (unitType == UnitType.INFANTRY) {
            utility *= weights.infantryDesire;
        } else if (unitType == UnitType.ENGINEER) {
            if (!state.hasEngineerUnit() && !state.hasEngineerInProduction()) {
                utility = weights.economyBaseWeight * weights.engineerGuaranteeMultiplier;
            } else {
                utility *= weights.engineerDesire;
            }
            if (state.hasDiscoveredNonHomeIsland() && !state.hasEngineerSwimmingOrOnNonHomeIsland()) {
                utility *= (1.0 + weights.engineerIslandHopBoost);
            }
        } else if (unitType == UnitType.TANK) {
            if (!state.hasTankUnit() && !state.hasTankInProduction()) {
                utility = weights.economyBaseWeight * weights.tankDesire * 1.5;
            } else {
                utility *= weights.tankDesire;
            }
        } else if (unitType == UnitType.TRANSPORT) {
            if (!state.hasTransportCapability() && state.isCoastalCity(city)) {
                utility = weights.economyBaseWeight * weights.coastalCityDesire * 2.0;
                if (state.hasDiscoveredNonHomeIsland()) {
                    utility *= 1.5;
                }
                if (state.getGameTimePercent() > 0.2) {
                    utility = weights.economyBaseWeight * weights.coastalCityDesire * 3.0;
                }
            } else if (state.isCoastalCity(city) && state.hasDiscoveredNonHomeIsland()
                    && !state.hasTransportEnRoute()
                    && state.getTransportsWithPassengers().isEmpty()
                    && state.countCitiesBuildingType(UnitType.TRANSPORT) == 0) {
                utility = weights.economyBaseWeight * weights.coastalCityDesire * 1.5;
            } else {
                utility *= weights.navalBaseWeight;
            }
        } else if (unitBase.isNavy()) {
            utility *= weights.navalBaseWeight;
        } else if (unitBase.isAir()) {
            utility *= weights.airStrikeDesire;
        } else if (unitBase.isTech() && unitType != UnitType.RESEARCH) {
            utility *= weights.icbmLaunchDesire;
            utility *= state.getGameTimePercent();
        }

        if (unitType == UnitType.TRANSPORT && state.isCoastalCity(city)
                && state.countCitiesBuildingType(UnitType.TRANSPORT) == 0) {
            utility *= (1.0 + weights.coastalCityDesire);
        }

        if (state.getGameTimePercent() < 0.3) {
            utility *= (1.0 + weights.earlyExpansionBonus);
        }

        return utility;
    }

    @Override
    public boolean execute() {
        // Use BUILD when city has no build or is building a non-unit (BASE/RESEARCH),
        // since non-units never "complete" and would block NEXT_BUILD from starting
        CityFieldToUpdateEnum field =
                (city.getBuild() != null && UnitBase.isUnit(city.getBuild()))
                        ? CityFieldToUpdateEnum.NEXT_BUILD
                        : CityFieldToUpdateEnum.BUILD;
        return cityService.updateCity(nation, city.getCoords(),
                field, unitType, null, false, null).isSuccess();
    }

    @Override
    public int getCommandPointCost() {
        return 0;
    }

    @Override
    public String getInvolvedCityKey() {
        return city.getCoords().x + "," + city.getCoords().y;
    }

    @Override
    public String getActionType() {
        return "SetCityProduction:" + unitType.name();
    }

    @Override
    public String describe() {
        String field = city.getBuild() != null ? "next build" : "build";
        return "Set " + city.getCoords() + " " + field + " to " + unitType;
    }
}
