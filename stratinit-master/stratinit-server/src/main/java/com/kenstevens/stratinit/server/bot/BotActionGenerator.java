package com.kenstevens.stratinit.server.bot;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.UnitBase;
import com.kenstevens.stratinit.server.bot.action.*;
import com.kenstevens.stratinit.server.service.CityService;
import com.kenstevens.stratinit.server.service.RelationService;
import com.kenstevens.stratinit.server.svc.MoveService;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BotActionGenerator {
    @Autowired
    private CityService cityService;
    @Autowired
    private MoveService moveService;
    @Autowired
    private RelationService relationService;

    public List<BotAction> generateActions(BotWorldState state, BotWeights weights) {
        List<BotAction> actions = new ArrayList<>();
        Nation nation = state.getNation();

        generateCityProductionActions(state, nation, actions);
        generateExpansionActions(state, nation, actions);
        generateMilitaryActions(state, nation, actions);
        generateDefenseActions(state, nation, actions);
        generateDiplomacyActions(state, nation, actions);

        return actions;
    }

    private void generateCityProductionActions(BotWorldState state, Nation nation, List<BotAction> actions) {
        for (City city : state.getMyCities()) {
            // Suggest building infantry early, tech centres if we have few
            if (UnitBase.isNotUnit(city.getBuild())) {
                actions.add(new SetCityProductionAction(city, UnitType.INFANTRY, nation, cityService));
            }
            // Suggest tech centre if tech is low and we have some cities
            if (state.getMyCities().size() >= 2 && state.getTech() < 4.0) {
                actions.add(new SetCityProductionAction(city, UnitType.RESEARCH, nation, cityService));
            }
            // Suggest engineer for expansion
            if (state.getMyCities().size() < 5) {
                actions.add(new SetCityProductionAction(city, UnitType.ENGINEER, nation, cityService));
            }
        }
    }

    private void generateExpansionActions(BotWorldState state, Nation nation, List<BotAction> actions) {
        int gameSize = state.getGame().getGamesize();

        for (Unit unit : state.getIdleLandUnits()) {
            if (unit.getType() == UnitType.ENGINEER && unit.getMobility() >= com.kenstevens.stratinit.type.Constants.MOB_COST_TO_CREATE_CITY) {
                // Engineer can build a city
                actions.add(new BuildCityWithEngineerAction(unit, cityService));
            }
            if (unit.getType() == UnitType.INFANTRY || unit.getType() == UnitType.TANK) {
                // Try moving toward undefended cities or unexplored areas
                // Simple heuristic: move in a direction away from our start
                SectorCoords unitCoords = unit.getCoords();
                // Generate a few candidate movement directions
                int[][] offsets = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}};
                for (int[] offset : offsets) {
                    int newX = (unitCoords.x + offset[0] * 2 + gameSize) % gameSize;
                    int newY = (unitCoords.y + offset[1] * 2 + gameSize) % gameSize;
                    SectorCoords target = new SectorCoords(newX, newY);
                    int distance = SectorCoords.distance(gameSize, unitCoords, target);
                    actions.add(new MoveUnitToExpandAction(unit, target, distance, nation, moveService));
                }
            }
        }
    }

    private void generateMilitaryActions(BotWorldState state, Nation nation, List<BotAction> actions) {
        List<Unit> enemies = state.getEnemyUnits();
        int gameSize = state.getGame().getGamesize();

        for (Unit myUnit : state.getIdleUnits()) {
            if (!myUnit.getUnitBase().isLand()) {
                continue;
            }
            for (Unit enemy : enemies) {
                if (!enemy.getUnitBase().isLand()) {
                    continue;
                }
                int distance = SectorCoords.distance(gameSize, myUnit.getCoords(), enemy.getCoords());
                // Only consider nearby enemies (within movement range)
                if (distance <= myUnit.getMobility()) {
                    actions.add(new AttackEnemyAction(myUnit, enemy, nation, moveService));
                }
            }
        }
    }

    private void generateDefenseActions(BotWorldState state, Nation nation, List<BotAction> actions) {
        List<City> undefended = state.getUndefendedCities();
        if (undefended.isEmpty()) {
            return;
        }

        int gameSize = state.getGame().getGamesize();

        for (Unit unit : state.getIdleLandUnits()) {
            for (City city : undefended) {
                int distance = SectorCoords.distance(gameSize, unit.getCoords(), city.getCoords());
                if (distance <= unit.getMobility() && distance > 0) {
                    actions.add(new DefendCityAction(unit, city, nation, moveService));
                }
            }
        }
    }

    private void generateDiplomacyActions(BotWorldState state, Nation nation, List<BotAction> actions) {
        Map<Nation, RelationType> myRelations = state.getMyRelations();
        Map<Nation, RelationType> theirRelations = state.getTheirRelations();

        for (Map.Entry<Nation, RelationType> entry : theirRelations.entrySet()) {
            Nation other = entry.getKey();
            RelationType theirStance = entry.getValue();
            RelationType myStance = myRelations.getOrDefault(other, RelationType.NEUTRAL);

            // If they're friendly toward us and we're neutral, consider being friendly back
            if (theirStance == RelationType.FRIENDLY && myStance == RelationType.NEUTRAL) {
                actions.add(new SetRelationAction(nation, other, RelationType.FRIENDLY, relationService));
            }
            // If they declared war, declare war back
            if (theirStance == RelationType.WAR && myStance != RelationType.WAR) {
                actions.add(new SetRelationAction(nation, other, RelationType.WAR, relationService));
            }
        }
    }
}
