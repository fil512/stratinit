package com.kenstevens.stratinit.server.bot;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.server.bot.action.*;
import com.kenstevens.stratinit.server.service.CityService;
import com.kenstevens.stratinit.server.service.RelationService;
import com.kenstevens.stratinit.server.svc.MoveService;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BotActionGenerator {
    @Autowired
    private CityService cityService;
    @Autowired
    private MoveService moveService;
    @Autowired
    private RelationService relationService;

    // Unit types that require a coastal city
    private static final Set<UnitType> NAVAL_UNIT_TYPES = EnumSet.of(
            UnitType.TRANSPORT, UnitType.PATROL, UnitType.DESTROYER,
            UnitType.BATTLESHIP, UnitType.SUBMARINE, UnitType.CARRIER,
            UnitType.CRUISER, UnitType.SUPPLY
    );

    // Air units (auto-convert city to airport)
    private static final Set<UnitType> AIR_UNIT_TYPES = EnumSet.of(
            UnitType.FIGHTER, UnitType.HEAVY_BOMBER, UnitType.NAVAL_BOMBER,
            UnitType.HELICOPTER, UnitType.CARGO_PLANE, UnitType.ZEPPELIN
    );

    // Tech units (auto-convert city to tech center)
    private static final Set<UnitType> STRATEGIC_UNIT_TYPES = EnumSet.of(
            UnitType.SATELLITE, UnitType.ICBM_1, UnitType.ICBM_2, UnitType.ICBM_3
    );

    public List<BotAction> generateActions(BotWorldState state, BotWeights weights) {
        List<BotAction> actions = new ArrayList<>();
        Nation nation = state.getNation();

        generateCityProductionActions(state, nation, actions);
        generateExpansionActions(state, nation, actions);
        generateMilitaryActions(state, nation, actions);
        generateDefenseActions(state, nation, actions);
        generateDiplomacyActions(state, nation, actions);
        generateNavalActions(state, nation, actions);
        generateAirActions(state, nation, actions);
        generateStrategicActions(state, nation, actions);

        return actions;
    }

    private void generateCityProductionActions(BotWorldState state, Nation nation, List<BotAction> actions) {
        for (City city : state.getMyCities()) {
            boolean isCoastal = state.isCoastalCity(city);

            // Always suggest land units
            actions.add(new SetCityProductionAction(city, UnitType.INFANTRY, nation, cityService));
            actions.add(new SetCityProductionAction(city, UnitType.TANK, nation, cityService));
            actions.add(new SetCityProductionAction(city, UnitType.RESEARCH, nation, cityService));
            actions.add(new SetCityProductionAction(city, UnitType.ENGINEER, nation, cityService));

            // Naval units: coastal cities only
            if (isCoastal) {
                for (UnitType navalType : NAVAL_UNIT_TYPES) {
                    actions.add(new SetCityProductionAction(city, navalType, nation, cityService));
                }
            }

            // Air units: any city (auto-converts to airport)
            for (UnitType airType : AIR_UNIT_TYPES) {
                actions.add(new SetCityProductionAction(city, airType, nation, cityService));
            }

            // Strategic units: any city (auto-converts to tech center)
            for (UnitType stratType : STRATEGIC_UNIT_TYPES) {
                actions.add(new SetCityProductionAction(city, stratType, nation, cityService));
            }
        }
    }

    private void generateExpansionActions(BotWorldState state, Nation nation, List<BotAction> actions) {
        int gameSize = state.getGame().getGamesize();
        World world = state.getWorld();

        for (Unit unit : state.getIdleLandUnits()) {
            // Skip land units riding on transports (at sea)
            Sector unitSector = world.getSectorOrNull(unit.getCoords());
            if (unitSector != null && unitSector.isWater()) {
                continue;
            }
            if (unit.getType() == UnitType.ENGINEER && unit.getMobility() >= com.kenstevens.stratinit.type.Constants.MOB_COST_TO_CREATE_CITY) {
                // Engineer can build a city
                actions.add(new BuildCityWithEngineerAction(unit, cityService));
            }
            if (unit.getType() == UnitType.INFANTRY || unit.getType() == UnitType.TANK) {
                generateSmartExpansionMoves(unit, state, nation, actions, true);
            }
        }

        // Naval expansion: idle naval units can explore water
        for (Unit unit : state.getIdleNavalUnits()) {
            generateSmartExpansionMoves(unit, state, nation, actions, false);
        }

        // Neutral city capture: send idle land units to free cities
        generateNeutralCityCaptureActions(state, nation, actions);
    }

    private void generateSmartExpansionMoves(Unit unit, BotWorldState state, Nation nation,
                                              List<BotAction> actions, boolean isLand) {
        int gameSize = state.getGame().getGamesize();
        World world = state.getWorld();
        SectorCoords unitCoords = unit.getCoords();

        // Find frontier: unscouted sectors adjacent to scouted sectors with matching terrain
        List<SectorCoords> frontierTargets = new ArrayList<>();
        for (SectorCoords scouted : findScoutedCoordsOfMatchingTerrain(state, world, gameSize, isLand)) {
            List<SectorCoords> neighbours = scouted.neighbours(gameSize);
            for (SectorCoords neighbour : neighbours) {
                if (!state.isExplored(neighbour)) {
                    // Target the scouted coord next to the frontier (unit can actually reach it)
                    frontierTargets.add(scouted);
                    break;
                }
            }
        }

        // Sort by distance to unit, take nearest 4
        frontierTargets.sort(Comparator.comparingInt(
                c -> SectorCoords.distance(gameSize, unitCoords, c)));
        int limit = Math.min(4, frontierTargets.size());
        for (int i = 0; i < limit; i++) {
            SectorCoords target = frontierTargets.get(i);
            int distance = SectorCoords.distance(gameSize, unitCoords, target);
            actions.add(new MoveUnitToExpandAction(unit, target, distance, nation, moveService));
        }

        // Fallback: if no frontier found, use directional moves
        if (frontierTargets.isEmpty()) {
            generateDirectionalMoves(unit, gameSize, nation, actions);
        }
    }

    private Set<SectorCoords> findScoutedCoordsOfMatchingTerrain(BotWorldState state, World world,
                                                                   int gameSize, boolean isLand) {
        Set<SectorCoords> matching = new HashSet<>();
        for (int x = 0; x < gameSize; x++) {
            for (int y = 0; y < gameSize; y++) {
                SectorCoords coords = new SectorCoords(x, y);
                if (!state.isExplored(coords)) {
                    continue;
                }
                Sector sector = world.getSectorOrNull(coords);
                if (sector == null) {
                    continue;
                }
                if (isLand && !sector.isWater()) {
                    matching.add(coords);
                } else if (!isLand && sector.isWater()) {
                    matching.add(coords);
                }
            }
        }
        return matching;
    }

    private void generateNeutralCityCaptureActions(BotWorldState state, Nation nation, List<BotAction> actions) {
        int gameSize = state.getGame().getGamesize();
        for (SectorCoords cityCoords : state.getNeutralCityCoords()) {
            for (Unit unit : state.getIdleLandUnits()) {
                int distance = SectorCoords.distance(gameSize, unit.getCoords(), cityCoords);
                if (distance <= unit.getMobility()) {
                    actions.add(new CaptureNeutralCityAction(unit, cityCoords, distance, nation, moveService));
                }
            }
        }
    }

    private void generateDirectionalMoves(Unit unit, int gameSize, Nation nation, List<BotAction> actions) {
        SectorCoords unitCoords = unit.getCoords();
        for (SectorCoords target : unitCoords.sectorsWithin(gameSize, 2, false)) {
            int distance = SectorCoords.distance(gameSize, unitCoords, target);
            if (distance == 2) {
                actions.add(new MoveUnitToExpandAction(unit, target, distance, nation, moveService));
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

    private void generateNavalActions(BotWorldState state, Nation nation, List<BotAction> actions) {
        List<Unit> enemies = state.getEnemyUnits();
        int gameSize = state.getGame().getGamesize();

        for (Unit myUnit : state.getIdleNavalUnits()) {
            // Naval combat: attack enemy naval units
            if (myUnit.getAttack() > 0) {
                for (Unit enemy : enemies) {
                    if (!enemy.getUnitBase().isNavy()) {
                        continue;
                    }
                    int distance = SectorCoords.distance(gameSize, myUnit.getCoords(), enemy.getCoords());
                    if (distance <= myUnit.getMobility()) {
                        actions.add(new AttackNavalAction(myUnit, enemy, nation, moveService));
                    }
                }
            }

            // Transport loading: move transport/carrier to pick up idle land units
            if (myUnit.getUnitBase().getCapacity() > 0) {
                Set<SectorCoords> landUnitCoords = new HashSet<>();
                for (Unit landUnit : state.getIdleLandUnits()) {
                    landUnitCoords.add(landUnit.getCoords());
                }
                for (SectorCoords coords : landUnitCoords) {
                    int distance = SectorCoords.distance(gameSize, myUnit.getCoords(), coords);
                    if (distance <= myUnit.getMobility() && distance > 0) {
                        actions.add(new LoadTransportAction(myUnit, coords, nation, moveService));
                    }
                }
            }
        }

        // Transport destination planning: sail loaded transports to other islands
        generateTransportDestinationActions(state, nation, actions);

        // Disembark: land units at sea step onto adjacent land
        generateDisembarkActions(state, nation, actions);
    }

    private record LandingCandidate(SectorCoords coords, int distance, boolean hasCityTarget) {}

    private void generateTransportDestinationActions(BotWorldState state, Nation nation, List<BotAction> actions) {
        List<Unit> loadedTransports = state.getLoadedTransports();
        if (loadedTransports.isEmpty()) {
            return;
        }

        int gameSize = state.getGame().getGamesize();
        World world = state.getWorld();
        int homeIslandId = state.getHomeIslandId();

        // Build set of city coords on non-home islands (enemy + neutral)
        Set<SectorCoords> targetCityCoords = new HashSet<>();
        for (City enemyCity : state.getEnemyCities()) {
            Sector s = world.getSectorOrNull(enemyCity.getCoords());
            if (s != null && s.getIsland() != homeIslandId) {
                targetCityCoords.add(enemyCity.getCoords());
            }
        }
        for (SectorCoords neutralCoords : state.getNeutralCityCoords()) {
            Sector s = world.getSectorOrNull(neutralCoords);
            if (s != null && s.getIsland() != homeIslandId) {
                targetCityCoords.add(neutralCoords);
            }
        }

        for (Unit transport : loadedTransports) {
            SectorCoords transportCoords = transport.getCoords();
            List<LandingCandidate> candidates = new ArrayList<>();

            // Find water sectors adjacent to non-home island coast
            for (int x = 0; x < gameSize; x++) {
                for (int y = 0; y < gameSize; y++) {
                    SectorCoords coords = new SectorCoords(x, y);
                    if (!state.isExplored(coords)) {
                        continue;
                    }
                    Sector sector = world.getSectorOrNull(coords);
                    if (sector == null || !sector.isWater()) {
                        continue;
                    }
                    // Check if adjacent to non-home island land
                    boolean adjacentToLand = false;
                    boolean adjacentToCity = false;
                    for (Sector neighbour : world.getNeighbours(coords)) {
                        if (!neighbour.isWater() && neighbour.getIsland() != homeIslandId) {
                            adjacentToLand = true;
                            if (targetCityCoords.contains(neighbour.getCoords())) {
                                adjacentToCity = true;
                            }
                        }
                    }
                    if (adjacentToLand) {
                        int dist = SectorCoords.distance(gameSize, transportCoords, coords);
                        candidates.add(new LandingCandidate(coords, dist, adjacentToCity));
                    }
                }
            }

            // Prioritize city targets, then sort by distance
            candidates.sort(Comparator.<LandingCandidate>comparingInt(c -> c.hasCityTarget ? 0 : 1)
                    .thenComparingInt(c -> c.distance));
            int limit = Math.min(3, candidates.size());
            for (int i = 0; i < limit; i++) {
                LandingCandidate c = candidates.get(i);
                actions.add(new MoveTransportToTargetAction(transport, c.coords, c.distance,
                        c.hasCityTarget, nation, moveService));
            }
        }
    }

    private void generateDisembarkActions(BotWorldState state, Nation nation, List<BotAction> actions) {
        World world = state.getWorld();
        for (Unit landUnit : state.getLandUnitsAtSea()) {
            for (Sector neighbour : world.getNeighbours(landUnit.getCoords())) {
                if (!neighbour.isWater()) {
                    boolean isCity = neighbour.getType() == SectorType.PLAYER_CITY
                            || neighbour.getType() == SectorType.NEUTRAL_CITY;
                    actions.add(new DisembarkUnitAction(landUnit, neighbour.getCoords(), isCity, nation, moveService));
                }
            }
        }
    }

    private void generateAirActions(BotWorldState state, Nation nation, List<BotAction> actions) {
        List<Unit> enemies = state.getEnemyUnits();
        List<City> enemyCities = state.getEnemyCities();
        int gameSize = state.getGame().getGamesize();

        for (Unit myUnit : state.getIdleAirUnits()) {
            // Air combat: attack enemy units
            if (myUnit.getAttack() > 0) {
                for (Unit enemy : enemies) {
                    int distance = SectorCoords.distance(gameSize, myUnit.getCoords(), enemy.getCoords());
                    if (distance <= myUnit.getMobility()) {
                        actions.add(new AttackWithAirAction(myUnit, enemy.getCoords(), nation, moveService, false));
                    }
                }
            }

            // Bombing: bombers target enemy cities
            if (myUnit.getUnitBase().getBombPercentage() > 0) {
                for (City enemyCity : enemyCities) {
                    int distance = SectorCoords.distance(gameSize, myUnit.getCoords(), enemyCity.getCoords());
                    if (distance <= myUnit.getMobility()) {
                        actions.add(new AttackWithAirAction(myUnit, enemyCity.getCoords(), nation, moveService, true));
                    }
                }
            }
        }
    }

    private void generateStrategicActions(BotWorldState state, Nation nation, List<BotAction> actions) {
        int gameSize = state.getGame().getGamesize();
        SectorCoords mapCenter = new SectorCoords(gameSize / 2, gameSize / 2);
        List<City> enemyCities = state.getEnemyCities();

        for (Unit unit : state.getLaunchableUnits()) {
            UnitBase unitBase = unit.getUnitBase();

            if (unit.getType() == UnitType.SATELLITE) {
                actions.add(new LaunchSatelliteAction(unit, mapCenter, nation, moveService));
            } else if (unitBase.getDevastates()) {
                // ICBM: target each enemy city
                for (City enemyCity : enemyCities) {
                    actions.add(new LaunchICBMAction(unit, enemyCity, nation, moveService));
                }
            }
        }
    }
}
