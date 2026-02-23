package com.kenstevens.stratinit.server.bot;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;

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
}
