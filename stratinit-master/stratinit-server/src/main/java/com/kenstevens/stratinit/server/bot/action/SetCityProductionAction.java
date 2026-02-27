package com.kenstevens.stratinit.server.bot.action;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.UnitBase;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;
import com.kenstevens.stratinit.server.bot.BotWeights;
import com.kenstevens.stratinit.server.bot.BotWorldState;
import com.kenstevens.stratinit.server.service.CityService;
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
        double utility = weights.economyBaseWeight;

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

        if (unitType == UnitType.RESEARCH) {
            if (!state.isAnyResearchCity()) {
                // Guarantee at least one city researches tech
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
        } else if (unitType == UnitType.TANK) {
            utility *= weights.tankDesire;
        } else if (unitBase.isNavy()) {
            utility *= weights.navalBaseWeight;
        } else if (unitBase.isAir()) {
            utility *= weights.airStrikeDesire;
        } else if (unitBase.isTech() && unitType != UnitType.RESEARCH) {
            utility *= weights.icbmLaunchDesire;
            utility *= state.getGameTimePercent();
        }

        // Transport boost: if coastal city and no city is building transports
        if (unitType == UnitType.TRANSPORT && state.isCoastalCity(city)
                && state.countCitiesBuildingType(UnitType.TRANSPORT) == 0) {
            utility *= (1.0 + weights.coastalCityDesire);
        }

        // Early game: prefer economy/expansion units
        if (state.getGameTimePercent() < 0.3) {
            utility *= (1.0 + weights.earlyExpansionBonus);
        }

        return utility;
    }

    @Override
    public boolean execute() {
        CityFieldToUpdateEnum field = city.getBuild() != null
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
    public String describe() {
        String field = city.getBuild() != null ? "next build" : "build";
        return "Set " + city.getCoords() + " " + field + " to " + unitType;
    }
}
