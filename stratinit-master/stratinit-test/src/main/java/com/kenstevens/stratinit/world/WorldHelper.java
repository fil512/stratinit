package com.kenstevens.stratinit.world;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.model.World;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class WorldHelper {
    private static final String[] types = {
            //           11111
            // 012345678901234
            "###....###.....", // 0
            "###....###.....", // 1
            "##C....C##.....", // 2
            "###....###.....", // 3
            "#S#....#S#.....", // 4
            "###....###.....", // 5
            "#S#....#S#.....", // 6
            "###....###.....", // 7
            "##C....C##.....", // 8
            "###....###.....", // 9
            "###....###.....", // 10
            "###....###.....", // 11
            "###....#S#.....", // 12
            "###....###.....", // 13
            "###....#S#.....", // 14
    };
    private static final String[] islands = {
            "000....111.....", // 0
            "000....111.....", // 1
            "000....111.....", // 2
            "000....111.....", // 3
            "000....111.....", // 4
            "000....111.....", // 5
            "000....111.....", // 6
            "000....111.....", // 7
            "000....111.....", // 8
            "000....111.....", // 9
            "000....111.....", // 10
            "000....111.....", // 11
            "000....121.....", // 12
            "000....111.....", // 13
            "000....121.....", // 14
    };

    private WorldHelper() {
    }

    public static void validateWorld(List<Sector> sectors) {
        int[] sectorCount = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] cityCount = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] startCityCount = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (Sector sector : sectors) {
            int island = sector.getIsland();
            if (island == Constants.UNASSIGNED) {
                continue;
            }
            ++sectorCount[island];
            assertNotNull(sector.getType());
            if (sector.getType() == SectorType.NEUTRAL_CITY) {
                ++cityCount[island];
            } else if (sector.getType() == SectorType.START_CITY) {
                ++startCityCount[island];
            }
        }
        for (int i = 0; i < 2; ++i) {
            assertTrue(sectorCount[i] > 10, "Sectors on island " + i + " > 10.  Was: " + sectorCount[i]);
        }
    }

    public static World newTestWorld(Game testGame) {
        return newTestWorld(testGame, types, islands);
    }

    public static World newTestWorld(Game testGame, String[] types, String[] islands) {
        World world = new World(testGame, true);
        populate(world, types, islands);
        return world;
    }

    private static void populate(World world, String[] types, String[] islands) {
        int y = 0;
        for (String typeRow : types) {
            int x = 0;
            for (char typeChar : typeRow.toCharArray()) {
                if (typeChar == '.') {
                    ++x;
                    continue;
                }
                Sector sector = world.getSector(x, y);
                char islandChar = islands[y].charAt(x);
                if (islandChar == '0') {
                    sector.setIsland(0);
                } else if (islandChar == '1') {
                    sector.setIsland(1);
                } else if (islandChar == '2') {
                    sector.setIsland(2);
                }
                if (typeChar == '#') {
                    sector.setType(SectorType.LAND);
                } else if (typeChar == 'C') {
                    sector.setType(SectorType.NEUTRAL_CITY);
                } else if (typeChar == 'S') {
                    sector.setType(SectorType.START_CITY);
                }
                ++x;
            }
            ++y;
        }
    }

}
