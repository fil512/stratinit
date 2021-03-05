package com.kenstevens.stratinit.client.server.rest.svc;

import com.google.common.collect.Maps;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TargetScore {
    public static final int CITY_VALUE = 24 * 2;
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final WorldSeenMap worldSeenMap;
    private final Collection<Unit> unitsSeen;
    private final Map<SectorCoords, Integer> sectorScore = Maps.newHashMap();

    public TargetScore(WorldSeenMap worldSeenMap, Collection<Unit> unitsSeen) {
        this.worldSeenMap = worldSeenMap;
        this.unitsSeen = unitsSeen;
    }

    public static int getMultiplier(RelationType myRelation) {
        if (myRelation == RelationType.ALLIED || myRelation == RelationType.ME) {
            return -2;
        } else if (myRelation == RelationType.FRIENDLY) {
            return -1;
        } else if (myRelation == RelationType.WAR) {
            return 2;
        } else {
            return 0;
        }
    }

    public void calculateScore() {
        for (SectorCoords coords : worldSeenMap.keySet()) {
            sectorScore.put(coords, 0);
            WorldSector worldSector = worldSeenMap.getWorldSector(coords);
            if (worldSector.isPlayerCity()) {
                int multiplier = getMultiplier(worldSector.getNation());
                addScore(coords, multiplier * CITY_VALUE);
            }
        }
        for (Unit unit : unitsSeen) {
            int multiplier = getMultiplier(unit.getNation());
            addScore(unit.getCoords(), multiplier * unit.getUnitBase().getProductionTime());
        }
    }

    private void addScore(SectorCoords coords, int increment) {
        Integer score = sectorScore.get(coords);
        if (score == null) {
            return;
        }
        sectorScore.put(coords, score + increment);
    }

    private int getMultiplier(Nation nation) {
        RelationType myRelation = worldSeenMap.getMyRelation(nation);

        return getMultiplier(myRelation);
    }

    public NukeTargetScore chooseTarget(Collection<Unit> nukes) {
        Set<SectorCoords> keySet = worldSeenMap.keySet();
        SectorCoords targetCoords = new SectorCoords(0, 0);
        int highScore = -1024;
        Unit bestNuke = null;
        for (SectorCoords target : keySet) {
            Unit nuke = getBestNukeInRange(target, nukes);
            if (nuke == null) {
                continue;
            }
            int score = calculateScore(nuke, target);
            if (score > highScore) {
                targetCoords = target;
                highScore = score;
                bestNuke = nuke;
            }
        }
        if (highScore < CITY_VALUE * 2 || bestNuke == null) {
            return null;
        }
        return new NukeTargetScore(targetCoords, highScore, bestNuke);
    }

    private int calculateScore(Unit nuke, SectorCoords targetCoords) {
        int score = 0;
        List<WorldSector> sectors = worldSeenMap.getSectorsWithin(targetCoords, nuke.getBlastRadius(), true);
        for (Sector sector : sectors) {
            score += sectorScore.get(sector.getCoords());
        }
        return score;
    }

    private Unit getBestNukeInRange(SectorCoords target, Collection<Unit> nukes) {
        int blast = -1;
        Unit retval = null;
        for (Unit nuke : nukes) {
            SectorCoords nukeCoords = nuke.getCoords();
            if (nukeCoords.distanceTo(worldSeenMap, target) <= nuke.getMobility()) {
                if (nuke.getBlastRadius() > blast) {
                    blast = nuke.getBlastRadius();
                    retval = nuke;
                }
            }
        }
        return retval;
    }
}
