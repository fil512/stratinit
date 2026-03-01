package com.kenstevens.stratinit.server.bot;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.server.bot.action.*;
import com.kenstevens.stratinit.type.Constants;
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
import java.util.stream.Collectors;

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
        int homeIslandId = state.getHomeIslandId();
        boolean hasUnexploredHome = state.hasUnexploredHomeIslandFrontier();

        for (Unit unit : state.getIdleLandUnits()) {
            // Skip land units riding on transports (at sea) — except engineers who can swim
            Sector unitSector = world.getSectorOrNull(unit.getCoords());
            if (unitSector != null && unitSector.isWater() && unit.getType() != UnitType.ENGINEER) {
                continue;
            }
            if (unit.getType() == UnitType.ENGINEER) {
                // Check if this is a good build site (regardless of current mobility)
                Sector engSector = world.getSectorOrNull(unit.getCoords());
                boolean serverAllows = engSector != null && cityService.canEstablishCity(nation, engSector);
                boolean spacedOut = state.distanceToNearestCity(unit.getCoords()) >= 2;
                boolean goodBuildSite = serverAllows && spacedOut;

                if (goodBuildSite) {
                    // At a valid build site — build if we have mobility, otherwise wait
                    if (unit.getMobility() >= com.kenstevens.stratinit.type.Constants.MOB_COST_TO_CREATE_CITY) {
                        actions.add(new BuildCityWithEngineerAction(unit, cityService, true));
                    }
                    // Don't generate move actions — let the engineer sit and recharge
                } else {
                    // Not at a build site — move toward one (engineers are builders, not scouts)
                    generateEngineerBuildSiteMoves(unit, state, nation, actions);
                    // Engineer swim to discovered non-home islands (only when not at a build site)
                    if (state.hasDiscoveredNonHomeIsland()) {
                        generateEngineerSwimActions(unit, state, nation, actions);
                    }
                }
            }
            if (unit.getType() == UnitType.INFANTRY || unit.getType() == UnitType.TANK) {
                // Home island exploration priority
                if (state.isOnHomeIsland(unit) && hasUnexploredHome) {
                    generateHomeIslandExpansionMoves(unit, state, nation, actions, homeIslandId);
                } else {
                    generateSmartExpansionMoves(unit, state, nation, actions, true, false);
                }
            }
        }

        // Naval expansion: idle naval units can explore water
        // Skip all transports — loaded ones use destination planning, empty ones use pre-positioning
        for (Unit unit : state.getIdleNavalUnits()) {
            if (unit.carriesUnits()) {
                continue;
            }
            generateSmartExpansionMoves(unit, state, nation, actions, false, false);
        }

        // Neutral city capture: send idle land units to free cities
        generateNeutralCityCaptureActions(state, nation, actions);

        // Infantry/tanks move to coast for transport pickup (always, to pre-position)
        generateMoveToCoastActions(state, nation, actions, false);

        // Transport loading guarantee: when idle empty transport exists,
        // generate high-priority actions to connect infantry to transport
        if (state.hasIdleEmptyTransport()) {
            generateTransportLoadingGuarantee(state, nation, actions);
        }
    }

    private void generateHomeIslandExpansionMoves(Unit unit, BotWorldState state, Nation nation,
                                                    List<BotAction> actions, int homeIslandId) {
        int gameSize = state.getGame().getGamesize();
        World world = state.getWorld();
        SectorCoords unitCoords = unit.getCoords();

        // Find frontier targets only on the home island (land sectors)
        List<SectorCoords> frontierTargets = new ArrayList<>();
        for (int x = 0; x < gameSize; x++) {
            for (int y = 0; y < gameSize; y++) {
                SectorCoords coords = new SectorCoords(x, y);
                if (!state.isExplored(coords)) continue;
                Sector sector = world.getSectorOrNull(coords);
                if (sector == null || sector.isWater() || sector.getIsland() != homeIslandId) continue;
                for (SectorCoords neighbour : coords.neighbours(gameSize)) {
                    if (!state.isExplored(neighbour)) {
                        frontierTargets.add(coords);
                        break;
                    }
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
            actions.add(new MoveUnitToExpandAction(unit, target, distance, nation, moveService, true));
        }

        // Fallback: if no home island frontier, use normal expansion
        if (frontierTargets.isEmpty()) {
            generateSmartExpansionMoves(unit, state, nation, actions, true, false);
        }
    }

    private void generateEngineerBuildSiteMoves(Unit unit, BotWorldState state, Nation nation,
                                                  List<BotAction> actions) {
        int gameSize = state.getGame().getGamesize();
        SectorCoords unitCoords = unit.getCoords();

        List<BotWorldState.BuildSite> sites = state.findBuildSites(2);
        if (!sites.isEmpty()) {
            // Move toward known build sites — sort by distance, take nearest 3
            sites.sort(Comparator.comparingInt(
                    s -> SectorCoords.distance(gameSize, unitCoords, s.coords())));
            int limit = Math.min(3, sites.size());
            for (int i = 0; i < limit; i++) {
                BotWorldState.BuildSite site = sites.get(i);
                int distance = SectorCoords.distance(gameSize, unitCoords, site.coords());
                actions.add(new MoveEngineerToBuildSiteAction(unit, site.coords(), distance,
                        site.coastal(), nation, moveService));
            }
        } else {
            // No known build sites — explore to discover them
            int homeIslandId = state.getHomeIslandId();
            if (state.isOnHomeIsland(unit) && state.hasUnexploredHomeIslandFrontier()) {
                generateHomeIslandExpansionMoves(unit, state, nation, actions, homeIslandId);
            } else {
                generateSmartExpansionMoves(unit, state, nation, actions, true, false);
            }
        }
    }

    private void generateEngineerSwimActions(Unit engineer, BotWorldState state, Nation nation,
                                               List<BotAction> actions) {
        int gameSize = state.getGame().getGamesize();
        World world = state.getWorld();
        int homeIslandId = state.getHomeIslandId();
        SectorCoords unitCoords = engineer.getCoords();

        // Compute island scores
        Map<Integer, Integer> islandScores = new HashMap<>();
        for (BotWorldState.DiscoveredIsland island : state.getDiscoveredIslands()) {
            int score = island.neutralCityCount() * 3 + island.enemyCityCount() + island.knownLandCount() / 5;
            islandScores.put(island.islandId(), Math.max(1, score));
        }

        // Find explored coastal land on scoreable non-home islands
        record SwimCandidate(SectorCoords coords, int distance, int islandScore) {}
        List<SwimCandidate> candidates = new ArrayList<>();
        for (int x = 0; x < gameSize; x++) {
            for (int y = 0; y < gameSize; y++) {
                SectorCoords coords = new SectorCoords(x, y);
                if (!state.isExplored(coords)) continue;
                Sector sector = world.getSectorOrNull(coords);
                if (sector == null || sector.isWater()) continue;
                if (sector.getIsland() == homeIslandId) continue;
                if (!islandScores.containsKey(sector.getIsland())) continue;
                // Must be coastal (so engineer can swim to it)
                if (!world.isCoastal(sector)) continue;
                int dist = SectorCoords.distance(gameSize, unitCoords, coords);
                candidates.add(new SwimCandidate(coords, dist, islandScores.get(sector.getIsland())));
            }
        }

        // Sort by score desc, then distance asc
        candidates.sort(Comparator.<SwimCandidate>comparingInt(c -> -c.islandScore)
                .thenComparingInt(c -> c.distance));
        int limit = Math.min(3, candidates.size());
        for (int i = 0; i < limit; i++) {
            SwimCandidate c = candidates.get(i);
            actions.add(new SwimEngineerToIslandAction(engineer, c.coords, c.distance,
                    c.islandScore, nation, moveService));
        }
    }

    private void generateMoveToCoastActions(BotWorldState state, Nation nation, List<BotAction> actions,
                                               boolean engineersOnly) {
        int gameSize = state.getGame().getGamesize();
        World world = state.getWorld();
        List<SectorCoords> coastalPoints = state.getCoastalPickupPoints();
        if (coastalPoints.isEmpty()) return;

        for (Unit unit : state.getIdleLandUnits()) {
            if (engineersOnly) {
                if (unit.getType() != UnitType.ENGINEER) continue;
            } else {
                if (unit.getType() != UnitType.INFANTRY && unit.getType() != UnitType.TANK) continue;
                // Only inland infantry/tanks on home island
                if (!state.isOnHomeIsland(unit)) continue;
            }
            Sector unitSector = world.getSectorOrNull(unit.getCoords());
            if (unitSector == null || unitSector.isWater()) continue;
            if (world.isCoastal(unitSector)) continue; // already on coast

            // Find nearest 2 coastal pickup points
            coastalPoints.sort(Comparator.comparingInt(
                    c -> SectorCoords.distance(gameSize, unit.getCoords(), c)));
            int limit = Math.min(2, coastalPoints.size());
            for (int i = 0; i < limit; i++) {
                SectorCoords target = coastalPoints.get(i);
                int distance = SectorCoords.distance(gameSize, unit.getCoords(), target);
                boolean transportNearby = state.hasNearbyTransport(target, 6);
                actions.add(new MoveToCoastForPickupAction(unit, target, distance, transportNearby, nation, moveService));
            }
        }
    }

    private void generateTransportLoadingGuarantee(BotWorldState state, Nation nation, List<BotAction> actions) {
        int gameSize = state.getGame().getGamesize();
        World world = state.getWorld();

        // Find idle empty transports
        Set<SectorCoords> loadedCoords = state.getLoadedTransports().stream()
                .map(Unit::getCoords).collect(Collectors.toSet());
        List<Unit> emptyTransports = state.getIdleNavalUnits().stream()
                .filter(u -> u.carriesUnits() && !loadedCoords.contains(u.getCoords()))
                .collect(Collectors.toList());
        if (emptyTransports.isEmpty()) return;

        // Find idle land units on land (infantry/tank)
        List<Unit> landUnits = new ArrayList<>(state.getIdleLandUnits().stream()
                .filter(u -> u.getType() == UnitType.INFANTRY || u.getType() == UnitType.TANK)
                .filter(u -> {
                    Sector s = world.getSectorOrNull(u.getCoords());
                    return s != null && !s.isWater();
                })
                .collect(Collectors.toList()));
        if (landUnits.isEmpty()) return;

        // Track which land units have been paired so each transport gets a unique unit
        Set<Integer> pairedUnitIds = new HashSet<>();

        for (Unit transport : emptyTransports) {
            // Find coastal land tiles adjacent to the transport (where a land unit could board from)
            List<SectorCoords> coastalTiles = new ArrayList<>();
            for (Sector neighbour : world.getNeighbours(transport.getCoords())) {
                if (!neighbour.isWater()) {
                    coastalTiles.add(neighbour.getCoords());
                }
            }
            if (coastalTiles.isEmpty()) continue;

            // Find nearest unpaired land unit to any of these coastal tiles
            Unit bestUnit = null;
            SectorCoords bestCoastalTile = null;
            int bestDist = Integer.MAX_VALUE;
            for (Unit landUnit : landUnits) {
                if (pairedUnitIds.contains(landUnit.getId())) continue;
                for (SectorCoords coastal : coastalTiles) {
                    int dist = SectorCoords.distance(gameSize, landUnit.getCoords(), coastal);
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestUnit = landUnit;
                        bestCoastalTile = coastal;
                    }
                }
            }
            if (bestUnit == null) continue;
            pairedUnitIds.add(bestUnit.getId());

            if (bestDist == 0) {
                // Land unit is already on coast next to transport — board it
                actions.add(new BoardTransportAction(bestUnit, transport.getCoords(), nation, moveService));
            } else {
                // Guarantee: move infantry toward the coastal tile near the transport
                // Uses high utility (engineerGuaranteeMultiplier) to beat normal expansion
                actions.add(new MoveToTransportGuaranteeAction(bestUnit, bestCoastalTile, bestDist,
                        nation, moveService));
            }

            // Also try to move transport toward nearest land unit (if it has water neighbors within range)
            for (Sector neighbour : world.getNeighbours(bestUnit.getCoords())) {
                if (!neighbour.isWater()) continue;
                int distToNeighbour = SectorCoords.distance(gameSize, transport.getCoords(), neighbour.getCoords());
                if (distToNeighbour <= transport.getMobility()) {
                    actions.add(new LoadTransportGuaranteeAction(transport, neighbour.getCoords(), nation, moveService));
                    break;
                }
            }
        }
    }

    private void generateSmartExpansionMoves(Unit unit, BotWorldState state, Nation nation,
                                              List<BotAction> actions, boolean isLand, boolean isHomeIsland) {
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
            actions.add(new MoveUnitToExpandAction(unit, target, distance, nation, moveService, isHomeIsland));
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
                // Engineers are builders, not captors — skip them
                if (unit.getType() == UnitType.ENGINEER) continue;
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
            // Engineers are builders, not fighters
            if (myUnit.getType() == UnitType.ENGINEER) continue;
            for (Unit enemy : enemies) {
                if (!enemy.getUnitBase().isLand()) {
                    continue;
                }
                int distance = SectorCoords.distance(gameSize, myUnit.getCoords(), enemy.getCoords());
                // Consider enemies within extended range (will take multiple turns to reach)
                if (distance <= myUnit.getMobility() * 3) {
                    actions.add(new AttackEnemyAction(myUnit, enemy, distance, nation, moveService));
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
            // Engineers are builders, not defenders
            if (unit.getType() == UnitType.ENGINEER) continue;
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
        World world = state.getWorld();

        for (Unit myUnit : state.getIdleNavalUnits()) {
            // Naval combat: attack enemy naval units
            if (myUnit.getAttack() > 0) {
                for (Unit enemy : enemies) {
                    if (!enemy.getUnitBase().isNavy()) {
                        continue;
                    }
                    int distance = SectorCoords.distance(gameSize, myUnit.getCoords(), enemy.getCoords());
                    if (distance <= myUnit.getMobility() * 3) {
                        actions.add(new AttackNavalAction(myUnit, enemy, distance, nation, moveService));
                    }
                }
            }

            // Transport loading: move transport to water tile adjacent to idle land units
            if (myUnit.getUnitBase().getCapacity() > 0) {
                for (Unit landUnit : state.getIdleLandUnits()) {
                    Sector landSector = world.getSectorOrNull(landUnit.getCoords());
                    if (landSector == null || landSector.isWater()) continue;
                    for (Sector neighbour : world.getNeighbours(landUnit.getCoords())) {
                        if (!neighbour.isWater()) continue;
                        int distance = SectorCoords.distance(gameSize, myUnit.getCoords(), neighbour.getCoords());
                        if (distance <= myUnit.getMobility()) {
                            actions.add(new LoadTransportAction(myUnit, neighbour.getCoords(), nation, moveService));
                            break;
                        }
                    }
                }
            }
        }

        // Board transport: land units step onto adjacent transport's water tile
        generateBoardTransportActions(state, nation, actions);

        // Transport destination planning: sail loaded transports to other islands
        generateTransportDestinationActions(state, nation, actions);

        // Empty transport pre-positioning: sail empty transports toward discovered islands
        generateEmptyTransportDestinationActions(state, nation, actions);

        // Disembark: land units at sea step onto adjacent land
        generateDisembarkActions(state, nation, actions);
    }

    private void generateBoardTransportActions(BotWorldState state, Nation nation, List<BotAction> actions) {
        World world = state.getWorld();
        Set<SectorCoords> transportPositions = state.getIdleTransportCoords();
        if (transportPositions.isEmpty()) return;

        for (Unit landUnit : state.getIdleLandUnits()) {
            // Engineers swim on their own, don't board transports
            if (landUnit.getType() == UnitType.ENGINEER) continue;
            Sector landSector = world.getSectorOrNull(landUnit.getCoords());
            if (landSector == null || landSector.isWater()) continue;
            for (Sector neighbour : world.getNeighbours(landUnit.getCoords())) {
                if (neighbour.isWater() && transportPositions.contains(neighbour.getCoords())) {
                    actions.add(new BoardTransportAction(landUnit, neighbour.getCoords(), nation, moveService));
                    break;
                }
            }
        }
    }

    private record LandingCandidate(SectorCoords coords, int distance, boolean hasCityTarget, int islandScore) {}

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

        // Compute island scores from discovered islands
        Map<Integer, Integer> islandScores = new HashMap<>();
        for (BotWorldState.DiscoveredIsland island : state.getDiscoveredIslands()) {
            if (island.hasMyPresence()) continue; // Skip islands where we already have presence
            int score = island.neutralCityCount() * 3 + island.enemyCityCount() + island.knownLandCount() / 5;
            islandScores.put(island.islandId(), Math.max(1, score));
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
                    int bestIslandScore = 1;
                    for (Sector neighbour : world.getNeighbours(coords)) {
                        if (!neighbour.isWater() && neighbour.getIsland() != homeIslandId) {
                            adjacentToLand = true;
                            int neighbourScore = islandScores.getOrDefault(neighbour.getIsland(), 1);
                            bestIslandScore = Math.max(bestIslandScore, neighbourScore);
                            if (targetCityCoords.contains(neighbour.getCoords())) {
                                adjacentToCity = true;
                            }
                        }
                    }
                    if (adjacentToLand) {
                        int dist = SectorCoords.distance(gameSize, transportCoords, coords);
                        candidates.add(new LandingCandidate(coords, dist, adjacentToCity, bestIslandScore));
                    }
                }
            }

            // Prioritize island score, then city targets, then distance
            candidates.sort(Comparator.<LandingCandidate>comparingInt(c -> -c.islandScore)
                    .thenComparingInt(c -> c.hasCityTarget ? 0 : 1)
                    .thenComparingInt(c -> c.distance));
            int limit = Math.min(3, candidates.size());
            for (int i = 0; i < limit; i++) {
                LandingCandidate c = candidates.get(i);
                actions.add(new MoveTransportToTargetAction(transport, c.coords, c.distance,
                        c.hasCityTarget, c.islandScore, nation, moveService));
            }
        }
    }

    private void generateEmptyTransportDestinationActions(BotWorldState state, Nation nation, List<BotAction> actions) {
        if (!state.hasDiscoveredNonHomeIsland()) return;

        int gameSize = state.getGame().getGamesize();
        World world = state.getWorld();
        int homeIslandId = state.getHomeIslandId();

        // Find idle empty transports (not loaded, no passengers at same location)
        Set<SectorCoords> passengerCoords = state.getTransportsWithPassengers().stream()
                .map(Unit::getCoords).collect(Collectors.toSet());
        Set<SectorCoords> loadedCoords = state.getLoadedTransports().stream()
                .map(Unit::getCoords).collect(Collectors.toSet());

        // Find land units waiting on coast (idle land units on land adjacent to water)
        Set<SectorCoords> coastalLandUnitCoords = new HashSet<>();
        for (Unit landUnit : state.getIdleLandUnits()) {
            if (landUnit.getType() == UnitType.ENGINEER) continue;
            Sector landSector = world.getSectorOrNull(landUnit.getCoords());
            if (landSector == null || landSector.isWater()) continue;
            if (world.isCoastal(landSector)) {
                coastalLandUnitCoords.add(landUnit.getCoords());
            }
        }

        List<Unit> emptyTransports = state.getIdleNavalUnits().stream()
                .filter(u -> u.carriesUnits()
                        && !loadedCoords.contains(u.getCoords())
                        && !passengerCoords.contains(u.getCoords()))
                // Don't send away transports adjacent to coast with waiting land units
                .filter(u -> {
                    for (Sector neighbour : world.getNeighbours(u.getCoords())) {
                        if (!neighbour.isWater() && coastalLandUnitCoords.contains(neighbour.getCoords())) {
                            return false; // transport should stay to load this unit
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
        if (emptyTransports.isEmpty()) return;

        // Build target city coords and island scores (same logic as loaded transport destinations)
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

        Map<Integer, Integer> islandScores = new HashMap<>();
        for (BotWorldState.DiscoveredIsland island : state.getDiscoveredIslands()) {
            if (island.hasMyPresence()) continue;
            int score = island.neutralCityCount() * 3 + island.enemyCityCount() + island.knownLandCount() / 5;
            islandScores.put(island.islandId(), Math.max(1, score));
        }

        for (Unit transport : emptyTransports) {
            SectorCoords transportCoords = transport.getCoords();
            List<LandingCandidate> candidates = new ArrayList<>();

            for (int x = 0; x < gameSize; x++) {
                for (int y = 0; y < gameSize; y++) {
                    SectorCoords coords = new SectorCoords(x, y);
                    if (!state.isExplored(coords)) continue;
                    Sector sector = world.getSectorOrNull(coords);
                    if (sector == null || !sector.isWater()) continue;

                    boolean adjacentToLand = false;
                    boolean adjacentToCity = false;
                    int bestIslandScore = 1;
                    for (Sector neighbour : world.getNeighbours(coords)) {
                        if (!neighbour.isWater() && neighbour.getIsland() != homeIslandId) {
                            adjacentToLand = true;
                            int neighbourScore = islandScores.getOrDefault(neighbour.getIsland(), 1);
                            bestIslandScore = Math.max(bestIslandScore, neighbourScore);
                            if (targetCityCoords.contains(neighbour.getCoords())) {
                                adjacentToCity = true;
                            }
                        }
                    }
                    if (adjacentToLand) {
                        int dist = SectorCoords.distance(gameSize, transportCoords, coords);
                        candidates.add(new LandingCandidate(coords, dist, adjacentToCity, bestIslandScore));
                    }
                }
            }

            candidates.sort(Comparator.<LandingCandidate>comparingInt(c -> -c.islandScore)
                    .thenComparingInt(c -> c.hasCityTarget ? 0 : 1)
                    .thenComparingInt(c -> c.distance));
            int limit = Math.min(3, candidates.size());
            for (int i = 0; i < limit; i++) {
                LandingCandidate c = candidates.get(i);
                actions.add(new MoveTransportToTargetAction(transport, c.coords, c.distance,
                        c.hasCityTarget, c.islandScore, true, nation, moveService));
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
            // Zeppelins scout instead of attacking (attack=0 anyway)
            if (myUnit.getType() == UnitType.ZEPPELIN) {
                generateZeppelinScoutActions(myUnit, state, nation, actions);
                continue;
            }

            // Air combat: attack enemy units (air has high mobility, use 2x range)
            if (myUnit.getAttack() > 0) {
                for (Unit enemy : enemies) {
                    int distance = SectorCoords.distance(gameSize, myUnit.getCoords(), enemy.getCoords());
                    if (distance <= myUnit.getMobility() * 2) {
                        actions.add(new AttackWithAirAction(myUnit, enemy.getCoords(), distance, nation, moveService, false));
                    }
                }
            }

            // Bombing: bombers target enemy cities
            if (myUnit.getUnitBase().getBombPercentage() > 0) {
                for (City enemyCity : enemyCities) {
                    int distance = SectorCoords.distance(gameSize, myUnit.getCoords(), enemyCity.getCoords());
                    if (distance <= myUnit.getMobility() * 2) {
                        actions.add(new AttackWithAirAction(myUnit, enemyCity.getCoords(), distance, nation, moveService, true));
                    }
                }
            }
        }
    }

    private void generateZeppelinScoutActions(Unit zeppelin, BotWorldState state, Nation nation,
                                                List<BotAction> actions) {
        int gameSize = state.getGame().getGamesize();
        SectorCoords zepCoords = zeppelin.getCoords();
        int mobility = zeppelin.getMobility();
        int sight = zeppelin.getSight();
        boolean hasFuelConstraint = zeppelin.requiresFuel();

        // Score all reachable positions by how many new squares they would reveal
        record ScoredTarget(SectorCoords coords, int revealCount, int distance, double waterRatio) {}
        List<ScoredTarget> candidates = new ArrayList<>();
        World world = state.getWorld();

        for (SectorCoords target : zepCoords.sectorsWithin(gameSize, mobility, false)) {
            int distToTarget = SectorCoords.distance(gameSize, zepCoords, target);
            if (distToTarget == 0) continue;

            // Fuel safety: ensure enough fuel to reach target and return to nearest city
            if (hasFuelConstraint) {
                int distFromTargetToCity = state.distanceToNearestCity(target);
                if (zeppelin.getFuel() - distToTarget < distFromTargetToCity + 2) {
                    continue;
                }
            }

            // Count unexplored squares and explored water within sight range of this position
            int revealCount = 0;
            int exploredCount = 0;
            int exploredWaterCount = 0;
            for (SectorCoords visible : target.sectorsWithin(gameSize, sight, true)) {
                if (!state.isExplored(visible)) {
                    revealCount++;
                } else {
                    exploredCount++;
                    Sector visibleSector = world.getSectorOrNull(visible);
                    if (visibleSector != null && visibleSector.isWater()) {
                        exploredWaterCount++;
                    }
                }
            }
            if (revealCount == 0) continue;

            double waterRatio = exploredCount > 0 ? (double) exploredWaterCount / exploredCount : 0.0;
            candidates.add(new ScoredTarget(target, revealCount, distToTarget, waterRatio));
        }

        // Sort by reveal count descending, then distance ascending
        candidates.sort(Comparator.<ScoredTarget>comparingInt(c -> -c.revealCount)
                .thenComparingInt(c -> c.distance));

        // Check for island-hop direction bonus
        Set<SectorCoords> nonHomeCities = state.getNonHomeIslandCityCoords();

        // Generate actions for top candidates
        int count = Math.min(4, candidates.size());
        for (int i = 0; i < count; i++) {
            ScoredTarget c = candidates.get(i);
            boolean isIslandHopDirection = false;
            if (!nonHomeCities.isEmpty()) {
                int currentMinDist = nonHomeCities.stream()
                        .mapToInt(city -> SectorCoords.distance(gameSize, zepCoords, city))
                        .min().orElse(Integer.MAX_VALUE);
                int targetMinDist = nonHomeCities.stream()
                        .mapToInt(city -> SectorCoords.distance(gameSize, c.coords, city))
                        .min().orElse(Integer.MAX_VALUE);
                isIslandHopDirection = targetMinDist < currentMinDist;
            }
            actions.add(new ZeppelinScoutAction(zeppelin, c.coords, c.distance,
                    c.revealCount, c.waterRatio, false, isIslandHopDirection, nation, moveService));
        }

        // If no exploration targets and fuel is low, return to nearest city
        if (count == 0 && hasFuelConstraint && zeppelin.getFuel() < zeppelin.getUnitBase().getMaxFuel() / 2) {
            SectorCoords nearestCity = state.nearestCityCoords(zepCoords);
            if (nearestCity != null) {
                int dist = SectorCoords.distance(gameSize, zepCoords, nearestCity);
                actions.add(new ZeppelinScoutAction(zeppelin, nearestCity, dist,
                        0, 0.0, true, nation, moveService));
            }
        }
    }

    private void generateStrategicActions(BotWorldState state, Nation nation, List<BotAction> actions) {
        List<City> enemyCities = state.getEnemyCities();

        for (Unit unit : state.getLaunchableUnits()) {
            UnitBase unitBase = unit.getUnitBase();

            if (unit.getType() == UnitType.SATELLITE) {
                List<SectorCoords> targets = findBestSatelliteTargets(state, 3);
                for (SectorCoords target : targets) {
                    actions.add(new LaunchSatelliteAction(unit, target, nation, moveService));
                }
            } else if (unitBase.getDevastates()) {
                // ICBM: target each enemy city
                for (City enemyCity : enemyCities) {
                    actions.add(new LaunchICBMAction(unit, enemyCity, nation, moveService));
                }
            }
        }
    }

    private List<SectorCoords> findBestSatelliteTargets(BotWorldState state, int count) {
        int gameSize = state.getGame().getGamesize();
        World world = state.getWorld();
        int step = 4;

        // Score each candidate by how many unexplored sectors it would reveal
        List<Map.Entry<SectorCoords, Integer>> candidates = new ArrayList<>();
        for (int x = 0; x < gameSize; x += step) {
            for (int y = 0; y < gameSize; y += step) {
                SectorCoords candidate = new SectorCoords(x, y);
                int unexplored = 0;
                for (Sector s : world.getSectorsWithin(candidate, Constants.SATELLITE_SIGHT)) {
                    if (!state.isExplored(s.getCoords())) {
                        unexplored++;
                    }
                }
                if (unexplored > 0) {
                    candidates.add(Map.entry(candidate, unexplored));
                }
            }
        }

        candidates.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        List<SectorCoords> result = new ArrayList<>();
        for (int i = 0; i < Math.min(count, candidates.size()); i++) {
            result.add(candidates.get(i).getKey());
        }

        // Fallback to map center if no unexplored areas found
        if (result.isEmpty()) {
            result.add(new SectorCoords(gameSize / 2, gameSize / 2));
        }

        return result;
    }
}
