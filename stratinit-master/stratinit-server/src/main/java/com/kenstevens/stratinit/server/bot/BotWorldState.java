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

    public BotWorldState(Nation nation, Game game, List<Unit> myUnits, List<City> myCities,
                         Collection<Unit> allVisibleUnits,
                         Map<Nation, RelationType> myRelations,
                         Map<Nation, RelationType> theirRelations) {
        this(nation, game, myUnits, myCities, allVisibleUnits, myRelations, theirRelations, System.currentTimeMillis());
    }

    public BotWorldState(Nation nation, Game game, List<Unit> myUnits, List<City> myCities,
                         Collection<Unit> allVisibleUnits,
                         Map<Nation, RelationType> myRelations,
                         Map<Nation, RelationType> theirRelations,
                         long nowMillis) {
        this.nation = nation;
        this.game = game;
        this.myUnits = myUnits;
        this.myCities = myCities;
        this.allVisibleUnits = allVisibleUnits;
        this.myRelations = myRelations;
        this.theirRelations = theirRelations;

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
}
