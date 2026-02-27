package com.kenstevens.stratinit.server.bot;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

import java.util.*;
import java.util.stream.Collectors;

public class BotWorldState {
    private final Nation nation;
    private final Game game;
    private final List<Unit> myUnits;
    private final List<City> myCities;
    private final Collection<Unit> allVisibleUnits;
    private final Map<Nation, RelationType> myRelations;
    private final Map<Nation, RelationType> theirRelations;
    private final double gameTimePercent;
    private final Set<SectorCoords> coastalCityCoords;
    private final List<City> enemyCities;
    private final World world;
    private final Set<SectorCoords> scoutedCoords;
    private final List<SectorCoords> neutralCityCoords;

    public BotWorldState(Nation nation, Game game, List<Unit> myUnits, List<City> myCities,
                         Collection<Unit> allVisibleUnits,
                         Map<Nation, RelationType> myRelations,
                         Map<Nation, RelationType> theirRelations,
                         Set<SectorCoords> coastalCityCoords,
                         List<City> enemyCities,
                         World world,
                         Set<SectorCoords> scoutedCoords,
                         List<SectorCoords> neutralCityCoords) {
        this(nation, game, myUnits, myCities, allVisibleUnits, myRelations, theirRelations,
                coastalCityCoords, enemyCities, world, scoutedCoords, neutralCityCoords,
                System.currentTimeMillis());
    }

    public BotWorldState(Nation nation, Game game, List<Unit> myUnits, List<City> myCities,
                         Collection<Unit> allVisibleUnits,
                         Map<Nation, RelationType> myRelations,
                         Map<Nation, RelationType> theirRelations,
                         Set<SectorCoords> coastalCityCoords,
                         List<City> enemyCities,
                         World world,
                         Set<SectorCoords> scoutedCoords,
                         List<SectorCoords> neutralCityCoords,
                         long nowMillis) {
        this.nation = nation;
        this.game = game;
        this.myUnits = myUnits;
        this.myCities = myCities;
        this.allVisibleUnits = allVisibleUnits;
        this.myRelations = myRelations;
        this.theirRelations = theirRelations;
        this.coastalCityCoords = coastalCityCoords;
        this.enemyCities = enemyCities;
        this.world = world;
        this.scoutedCoords = scoutedCoords;
        this.neutralCityCoords = neutralCityCoords;

        // Calculate game time progress (0.0 = start, 1.0 = end)
        if (game.getStartTime() != null && game.getEnds() != null) {
            long total = game.getEnds().getTime() - game.getStartTime().getTime();
            long elapsed = nowMillis - game.getStartTime().getTime();
            this.gameTimePercent = total > 0 ? Math.min(1.0, Math.max(0.0, (double) elapsed / total)) : 0.0;
        } else {
            this.gameTimePercent = 0.0;
        }
    }

    public Nation getNation() {
        return nation;
    }

    public Game getGame() {
        return game;
    }

    public List<Unit> getMyUnits() {
        return myUnits;
    }

    public List<City> getMyCities() {
        return myCities;
    }

    public List<Unit> getIdleUnits() {
        return myUnits.stream()
                .filter(u -> u.isAlive() && u.getUnitMove() == null && u.getMobility() > 0)
                .collect(Collectors.toList());
    }

    public List<Unit> getIdleLandUnits() {
        return getIdleUnits().stream()
                .filter(u -> u.getUnitBase().isLand())
                .collect(Collectors.toList());
    }

    public List<Unit> getIdleNavalUnits() {
        return getIdleUnits().stream()
                .filter(u -> u.getUnitBase().isNavy())
                .collect(Collectors.toList());
    }

    public List<Unit> getIdleAirUnits() {
        return getIdleUnits().stream()
                .filter(u -> u.getUnitBase().isAir())
                .collect(Collectors.toList());
    }

    public List<Unit> getLaunchableUnits() {
        return myUnits.stream()
                .filter(u -> u.isAlive() && u.getUnitBase().isLaunchable())
                .collect(Collectors.toList());
    }

    public List<City> getUndefendedCities() {
        Set<SectorCoords> unitLocations = myUnits.stream()
                .filter(u -> u.isAlive() && u.getUnitBase().isLand())
                .map(Unit::getCoords)
                .collect(Collectors.toSet());
        return myCities.stream()
                .filter(c -> !unitLocations.contains(c.getCoords()))
                .collect(Collectors.toList());
    }

    public List<Unit> getEnemyUnits() {
        return allVisibleUnits.stream()
                .filter(u -> !u.getNation().equals(nation))
                .filter(u -> {
                    RelationType rel = myRelations.get(u.getNation());
                    return rel == null || rel == RelationType.WAR;
                })
                .collect(Collectors.toList());
    }

    public List<City> getEnemyCities() {
        return enemyCities;
    }

    public boolean isCoastalCity(City city) {
        return coastalCityCoords.contains(city.getCoords());
    }

    public boolean hasEnoughCP(int cost) {
        return nation.getCommandPoints() >= cost;
    }

    public int getCommandPoints() {
        return nation.getCommandPoints();
    }

    public double getGameTimePercent() {
        return gameTimePercent;
    }

    public Map<Nation, RelationType> getMyRelations() {
        return myRelations;
    }

    public Map<Nation, RelationType> getTheirRelations() {
        return theirRelations;
    }

    public double getTech() {
        return nation.getTech();
    }

    public World getWorld() {
        return world;
    }

    public boolean isExplored(SectorCoords coords) {
        return scoutedCoords.contains(coords);
    }

    public List<SectorCoords> getNeutralCityCoords() {
        return neutralCityCoords;
    }

    public int getHomeIslandId() {
        Map<Integer, Integer> islandCounts = new HashMap<>();
        for (City city : myCities) {
            Sector sector = world.getSectorOrNull(city.getCoords());
            if (sector != null && sector.getIsland() >= 0) {
                islandCounts.merge(sector.getIsland(), 1, Integer::sum);
            }
        }
        return islandCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(-1);
    }

    public Set<SectorCoords> getIdleTransportCoords() {
        return getIdleNavalUnits().stream()
                .filter(Unit::carriesUnits)
                .map(Unit::getCoords)
                .collect(Collectors.toSet());
    }

    public List<Unit> getLoadedTransports() {
        Set<SectorCoords> landUnitCoords = new HashSet<>();
        for (Unit u : myUnits) {
            if (u.isAlive() && u.getUnitBase().isLand()) {
                Sector sector = world.getSectorOrNull(u.getCoords());
                if (sector != null && sector.isWater()) {
                    landUnitCoords.add(u.getCoords());
                }
            }
        }
        return getIdleNavalUnits().stream()
                .filter(u -> u.carriesUnits() && landUnitCoords.contains(u.getCoords()))
                .collect(Collectors.toList());
    }

    public List<Unit> getLandUnitsAtSea() {
        return getIdleUnits().stream()
                .filter(u -> u.getUnitBase().isLand())
                .filter(u -> {
                    Sector sector = world.getSectorOrNull(u.getCoords());
                    return sector != null && sector.isWater();
                })
                .collect(Collectors.toList());
    }

    public int countLandUnitsInRangeOf(SectorCoords target) {
        return (int) getIdleLandUnits().stream()
                .filter(u -> u.getCoords().distanceTo(world, target) <= u.getMobility())
                .count();
    }

    public int countAirUnitsInRangeOf(SectorCoords target) {
        return (int) getIdleAirUnits().stream()
                .filter(u -> u.getAttack() > 0)
                .filter(u -> u.getCoords().distanceTo(world, target) <= u.getMobility())
                .count();
    }

    public boolean hasNearbyTransport(SectorCoords target, int radius) {
        return myUnits.stream()
                .filter(u -> u.isAlive() && u.carriesUnits())
                .anyMatch(u -> u.getCoords().distanceTo(world, target) <= radius);
    }

    public boolean hasLandThreatNear(SectorCoords target) {
        return countLandUnitsInRangeOf(target) > 0;
    }

    // --- Island expansion query methods ---

    public int distanceToNearestCity(SectorCoords coords) {
        int minDist = Integer.MAX_VALUE;
        for (City city : myCities) {
            int dist = coords.distanceTo(world, city.getCoords());
            if (dist < minDist) {
                minDist = dist;
            }
        }
        return minDist;
    }

    public SectorCoords nearestCityCoords(SectorCoords coords) {
        SectorCoords nearest = null;
        int minDist = Integer.MAX_VALUE;
        for (City city : myCities) {
            int dist = coords.distanceTo(world, city.getCoords());
            if (dist < minDist) {
                minDist = dist;
                nearest = city.getCoords();
            }
        }
        return nearest;
    }

    public boolean hasUnexploredHomeIslandFrontier() {
        int homeIsland = getHomeIslandId();
        if (homeIsland < 0) return false;
        int gameSize = game.getGamesize();
        for (int x = 0; x < gameSize; x++) {
            for (int y = 0; y < gameSize; y++) {
                SectorCoords coords = new SectorCoords(x, y);
                if (!isExplored(coords)) continue;
                Sector sector = world.getSectorOrNull(coords);
                if (sector == null || sector.isWater() || sector.getIsland() != homeIsland) continue;
                for (SectorCoords neighbour : coords.neighbours(gameSize)) {
                    if (!isExplored(neighbour)) return true;
                }
            }
        }
        return false;
    }

    public List<SectorCoords> getNeutralCityCoordsOnIsland(int islandId) {
        return neutralCityCoords.stream()
                .filter(coords -> {
                    Sector s = world.getSectorOrNull(coords);
                    return s != null && s.getIsland() == islandId;
                })
                .collect(Collectors.toList());
    }

    public boolean isAnyResearchCity() {
        return myCities.stream().anyMatch(c -> c.getBuild() == UnitType.RESEARCH);
    }

    public long countCitiesBuildingType(UnitType unitType) {
        return myCities.stream().filter(c -> c.getBuild() == unitType).count();
    }

    public boolean hasCoastalCity() {
        return myCities.stream().anyMatch(c -> coastalCityCoords.contains(c.getCoords()));
    }

    public List<SectorCoords> findCoastalBuildSites(int minDistFromCities) {
        int gameSize = game.getGamesize();
        List<SectorCoords> sites = new ArrayList<>();
        for (int x = 0; x < gameSize; x++) {
            for (int y = 0; y < gameSize; y++) {
                SectorCoords coords = new SectorCoords(x, y);
                if (!isExplored(coords)) continue;
                Sector sector = world.getSectorOrNull(coords);
                if (sector == null || sector.isWater()) continue;
                if (!world.isCoastal(sector)) continue;
                if (distanceToNearestCity(coords) < minDistFromCities) continue;
                sites.add(coords);
            }
        }
        return sites;
    }

    public List<SectorCoords> getCoastalPickupPoints() {
        int homeIsland = getHomeIslandId();
        if (homeIsland < 0) return Collections.emptyList();
        int gameSize = game.getGamesize();
        List<SectorCoords> points = new ArrayList<>();
        for (int x = 0; x < gameSize; x++) {
            for (int y = 0; y < gameSize; y++) {
                SectorCoords coords = new SectorCoords(x, y);
                if (!isExplored(coords)) continue;
                Sector sector = world.getSectorOrNull(coords);
                if (sector == null || sector.isWater() || sector.getIsland() != homeIsland) continue;
                if (world.isCoastal(sector)) {
                    points.add(coords);
                }
            }
        }
        return points;
    }

    public boolean hasTransportCapability() {
        return myUnits.stream().anyMatch(u -> u.isAlive() && u.carriesUnits())
                || myCities.stream().anyMatch(c -> c.getBuild() == UnitType.TRANSPORT);
    }

    public List<Unit> getTransportsWithPassengers() {
        Set<SectorCoords> landAtSeaCoords = myUnits.stream()
                .filter(u -> u.isAlive() && u.getUnitBase().isLand())
                .filter(u -> {
                    Sector sector = world.getSectorOrNull(u.getCoords());
                    return sector != null && sector.isWater();
                })
                .map(Unit::getCoords)
                .collect(Collectors.toSet());
        return myUnits.stream()
                .filter(u -> u.isAlive() && u.carriesUnits())
                .filter(u -> landAtSeaCoords.contains(u.getCoords()))
                .collect(Collectors.toList());
    }

    public boolean hasTransportEnRoute() {
        return myUnits.stream()
                .filter(u -> u.isAlive() && u.carriesUnits())
                .anyMatch(u -> u.getUnitMove() != null);
    }

    public boolean hasIdleEmptyTransport() {
        Set<SectorCoords> landAtSeaCoords = myUnits.stream()
                .filter(u -> u.isAlive() && u.getUnitBase().isLand())
                .filter(u -> {
                    Sector sector = world.getSectorOrNull(u.getCoords());
                    return sector != null && sector.isWater();
                })
                .map(Unit::getCoords)
                .collect(Collectors.toSet());
        return getIdleNavalUnits().stream()
                .filter(u -> u.carriesUnits())
                .anyMatch(u -> !landAtSeaCoords.contains(u.getCoords()));
    }

    public boolean isOnHomeIsland(Unit unit) {
        int homeIsland = getHomeIslandId();
        if (homeIsland < 0) return false;
        Sector sector = world.getSectorOrNull(unit.getCoords());
        return sector != null && sector.getIsland() == homeIsland;
    }

    public boolean hasEngineerUnit() {
        return myUnits.stream().anyMatch(u -> u.isAlive() && u.getType() == UnitType.ENGINEER);
    }

    public boolean hasEngineerInProduction() {
        return myCities.stream().anyMatch(c -> c.getBuild() == UnitType.ENGINEER || c.getNextBuild() == UnitType.ENGINEER);
    }

    public boolean hasTankUnit() {
        return myUnits.stream().anyMatch(u -> u.isAlive() && u.getType() == UnitType.TANK);
    }

    public boolean hasTankInProduction() {
        return myCities.stream().anyMatch(c -> c.getBuild() == UnitType.TANK || c.getNextBuild() == UnitType.TANK);
    }

    public record DiscoveredIsland(int islandId, int knownLandCount, int neutralCityCount, int enemyCityCount, boolean hasMyPresence) {}

    public boolean hasDiscoveredNonHomeIsland() {
        int homeIsland = getHomeIslandId();
        if (homeIsland < 0) return false;
        for (SectorCoords coords : scoutedCoords) {
            Sector sector = world.getSectorOrNull(coords);
            if (sector != null && !sector.isWater() && sector.getIsland() >= 0 && sector.getIsland() != homeIsland) {
                return true;
            }
        }
        return false;
    }

    public List<DiscoveredIsland> getDiscoveredIslands() {
        int homeIsland = getHomeIslandId();
        Map<Integer, Integer> landCounts = new HashMap<>();
        Map<Integer, Integer> neutralCounts = new HashMap<>();
        Map<Integer, Integer> enemyCounts = new HashMap<>();
        Set<Integer> myPresenceIslands = new HashSet<>();

        Set<SectorCoords> neutralCitySet = new HashSet<>(neutralCityCoords);
        Set<SectorCoords> enemyCitySet = enemyCities.stream()
                .map(City::getCoords).collect(Collectors.toSet());
        Set<SectorCoords> myCitySet = myCities.stream()
                .map(City::getCoords).collect(Collectors.toSet());

        for (SectorCoords coords : scoutedCoords) {
            Sector sector = world.getSectorOrNull(coords);
            if (sector == null || sector.isWater() || sector.getIsland() < 0 || sector.getIsland() == homeIsland) continue;
            int islandId = sector.getIsland();
            landCounts.merge(islandId, 1, Integer::sum);
            if (neutralCitySet.contains(coords)) {
                neutralCounts.merge(islandId, 1, Integer::sum);
            }
            if (enemyCitySet.contains(coords)) {
                enemyCounts.merge(islandId, 1, Integer::sum);
            }
            if (myCitySet.contains(coords)) {
                myPresenceIslands.add(islandId);
            }
        }
        // Also check if we have units on these islands
        for (Unit unit : myUnits) {
            if (!unit.isAlive()) continue;
            Sector sector = world.getSectorOrNull(unit.getCoords());
            if (sector != null && !sector.isWater() && sector.getIsland() >= 0 && sector.getIsland() != homeIsland) {
                myPresenceIslands.add(sector.getIsland());
            }
        }

        List<DiscoveredIsland> islands = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : landCounts.entrySet()) {
            int id = entry.getKey();
            islands.add(new DiscoveredIsland(id, entry.getValue(),
                    neutralCounts.getOrDefault(id, 0),
                    enemyCounts.getOrDefault(id, 0),
                    myPresenceIslands.contains(id)));
        }
        return islands;
    }

    public int countMyCitiesOnIsland(int islandId) {
        return (int) myCities.stream()
                .filter(c -> {
                    Sector s = world.getSectorOrNull(c.getCoords());
                    return s != null && s.getIsland() == islandId;
                })
                .count();
    }
}
