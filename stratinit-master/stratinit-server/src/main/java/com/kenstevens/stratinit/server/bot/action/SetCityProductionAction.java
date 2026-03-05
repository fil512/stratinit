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
        BotPersonality personality = state.getNation().getBotPersonality();
        if (personality != null) {
            return computePersonalityUtility(state, personality);
        }

        return computeDefaultUtility(state, weights, unitBase);
    }

    private double computePersonalityUtility(BotWorldState state, BotPersonality personality) {
        int cityCount = state.getMyCities().size();
        double tech = state.getTech();

        return switch (personality) {
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
        // All phases: ensure at least 1 transport from coastal cities
        if (state.isCoastalCity(city) && !state.hasTransportCapability()) {
            return unitType == UnitType.TRANSPORT ? 12.0 : 0;
        }
        // Phase 2 (>= 4 cities, tech < 10): Research-heavy, but build some military
        if (tech < 10) {
            if (unitType == UnitType.RESEARCH) return 10.0;
            if (unitType == UnitType.INFANTRY) return 4.0;
            return 0;
        }
        // Phase 3 (tech >= 10): Mix of strategic + military
        if (unitType == UnitType.HEAVY_BOMBER) return 9.0;
        if (unitType == UnitType.SATELLITE) return 8.0;
        if (unitType == UnitType.ICBM_3 && tech >= 16) return 8.0;
        if (unitType == UnitType.INFANTRY) return 5.0;
        if (unitType == UnitType.RESEARCH) return 6.0;
        if (unitType == UnitType.TRANSPORT && state.isCoastalCity(city)
                && state.countCitiesBuildingType(UnitType.TRANSPORT) == 0) return 7.0;
        return 0;
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
            // Also build destroyers to escort transports
            if (unitType == UnitType.DESTROYER && state.countAliveUnitsOfType(UnitType.DESTROYER) == 0) return 7.0;
            return unitType == UnitType.INFANTRY ? 10.0 : (unitType == UnitType.TRANSPORT ? 3.0 : 0);
        }
        // Non-coastal cities: infantry + some tanks and fighters
        if (unitType == UnitType.INFANTRY) return 10.0;
        if (unitType == UnitType.TANK && !state.hasTankUnit() && !state.hasTankInProduction()) return 8.0;
        if (unitType == UnitType.FIGHTER && state.countAliveUnitsOfType(UnitType.FIGHTER) == 0
                && state.countCitiesBuildingType(UnitType.FIGHTER) == 0) return 6.0;
        return 0;
    }

    private double computeBoomUtility(BotWorldState state) {
        int cityCount = state.getMyCities().size();
        double tech = state.getTech();
        // Phase 1 (< 4 cities): Engineers to expand
        if (cityCount < 4) {
            if (unitType == UnitType.ENGINEER) return 10.0;
            if (unitType == UnitType.ZEPPELIN && !state.hasZeppelinUnit() && !state.hasZeppelinInProduction()) return 8.0;
            return 0;
        }
        // Ensure transport from coastal cities
        if (state.isCoastalCity(city) && !state.hasTransportCapability()) {
            return unitType == UnitType.TRANSPORT ? 12.0 : 0;
        }
        // Phase 2 (>= 4 cities): Balanced economy + military
        if (state.isCoastalCity(city)) {
            if (unitType == UnitType.TRANSPORT && state.countCitiesBuildingType(UnitType.TRANSPORT) == 0) return 8.0;
            if (unitType == UnitType.BATTLESHIP && state.countAliveUnitsOfType(UnitType.BATTLESHIP) == 0
                    && state.countCitiesBuildingType(UnitType.BATTLESHIP) == 0) return 7.0;
            if (unitType == UnitType.ZEPPELIN) return 6.0;
            if (unitType == UnitType.INFANTRY) return 5.0;
            return 0;
        }
        // Non-coastal: engineers, research, military mix
        if (unitType == UnitType.ENGINEER && state.countAliveUnitsOfType(UnitType.ENGINEER) < 2) return 10.0;
        if (unitType == UnitType.RESEARCH) return 8.0;
        if (unitType == UnitType.HEAVY_BOMBER && tech >= 4) return 7.0;
        if (unitType == UnitType.INFANTRY) return 5.0;
        return 0;
    }

    private double computeTurtleUtility(BotWorldState state, int cityCount) {
        // Phase 1 (< 2 engineers ever built): build ENGINEER
        long engineerCount = state.countAliveUnitsOfType(UnitType.ENGINEER);
        long engineerBuilding = state.countCitiesBuildingType(UnitType.ENGINEER);
        if ((engineerCount + engineerBuilding) < 2) {
            return unitType == UnitType.ENGINEER ? 10.0 : 0;
        }
        // Ensure transport from coastal cities
        if (state.isCoastalCity(city) && !state.hasTransportCapability()) {
            return unitType == UnitType.TRANSPORT ? 12.0 : 0;
        }
        // Phase 2: Alternate RESEARCH / INFANTRY / FIGHTER + transport from coast
        if (state.isCoastalCity(city)) {
            if (unitType == UnitType.TRANSPORT && state.countCitiesBuildingType(UnitType.TRANSPORT) == 0) return 7.0;
            if (unitType == UnitType.DESTROYER && state.countAliveUnitsOfType(UnitType.DESTROYER) == 0
                    && state.countCitiesBuildingType(UnitType.DESTROYER) == 0) return 6.0;
        }
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
            long aliveEngineers = state.countAliveUnitsOfType(UnitType.ENGINEER);
            long buildingEngineers = state.countCitiesBuildingType(UnitType.ENGINEER);
            if (aliveEngineers == 0 && buildingEngineers == 0) {
                utility = weights.economyBaseWeight * weights.engineerGuaranteeMultiplier;
            } else if (aliveEngineers + buildingEngineers >= 2) {
                utility = 0;
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
        } else if (unitType == UnitType.ZEPPELIN) {
            utility *= weights.zeppelinScoutDesire;
            if (!state.hasZeppelinUnit() && !state.hasZeppelinInProduction()) {
                utility *= 2.0;
            }
        } else if (unitType == UnitType.HEAVY_BOMBER) {
            utility *= weights.airStrikeDesire;
            if (state.hasEnemyAtWar() && state.countAliveUnitsOfType(UnitType.HEAVY_BOMBER) == 0
                    && state.countCitiesBuildingType(UnitType.HEAVY_BOMBER) == 0) {
                utility = weights.militaryBaseWeight * weights.airStrikeDesire * 1.5;
            }
        } else if (unitType == UnitType.BATTLESHIP) {
            utility *= weights.navalBaseWeight;
            if (state.isCoastalCity(city) && state.hasEnemyAtWar()
                    && state.countAliveUnitsOfType(UnitType.BATTLESHIP) == 0
                    && state.countCitiesBuildingType(UnitType.BATTLESHIP) == 0) {
                utility = weights.navalBaseWeight * weights.bombardCityDesire;
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
