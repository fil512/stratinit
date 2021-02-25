package com.kenstevens.stratinit.server.remote.commands;

import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.server.remote.StratInitWebBase;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JoinGameTest extends StratInitWebBase {
    @BeforeEach
    public void doJoinGame() {
        joinGamePlayerMe();
    }

    @Test
    public void getJoinedGames() {
        List<SIGame> games = stratInit.getJoinedGames().getValue();
        assertEquals(1, games.size());
        assertEquals(1, games.get(0).players);
    }

    @Test
    public void getSectors() {
        List<SISector> sectors = stratInit.getSectors().getValue();
        assertEquals(49, sectors.size());
    }

    @Test
    public void getCities() {
        List<SICity> cities = stratInit.getCities().getValue();
        assertEquals(2, cities.size());
        SICity fort;
        SICity tech;
        if (cities.get(0).type == CityType.FORT) {
            fort = cities.get(0);
            tech = cities.get(1);
        } else {
            fort = cities.get(1);
            tech = cities.get(0);
        }
        assertEquals(CityType.FORT, fort.type);
        assertEquals(CityType.TECH, tech.type);
    }


    @Test
    public void getUnits() {
        List<SIUnit> units = stratInit.getUnits().getValue();
        assertEquals(5, units.size());
        int infantry = 0;
        int zeppelins = 0;
        for (SIUnit unit : units) {
            if (unit.type == UnitType.INFANTRY) {
                ++infantry;
                assertEquals(UnitBase.getUnitBase(UnitType.INFANTRY).getMobility() * 3, unit.mobility);
            } else if (unit.type == UnitType.ZEPPELIN) {
                ++zeppelins;
                assertEquals(UnitBase.getUnitBase(UnitType.ZEPPELIN).getMobility() * 3, unit.mobility);
            }
            assertEquals(nationMeId, unit.nationId);
        }
        assertEquals(4, infantry);
        assertEquals(1, zeppelins);
    }

    @Test
    public void getNations() {
        List<SINation> nations = stratInit.getNations().getValue();
        assertEquals(1, nations.size());
    }


}
