package com.kenstevens.stratinit.server.rest.commands;

import com.kenstevens.stratinit.BaseStratInitControllerTest;
import com.kenstevens.stratinit.dto.SICityUpdate;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.UpdateCityJson;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BuildUnitTest extends BaseStratInitControllerTest {
    private SICityUpdate sicity;

    @BeforeEach
    public void doJoinGame() throws InterruptedException {
        joinGamePlayerMe();
        List<SICityUpdate> cities = stratInitController.getCities().getValue();
        sicity = cities.get(0);
    }

    @Test
    public void airport() {
        sicity.build = UnitType.FIGHTER;
        Result<SICityUpdate> result = stratInitController.updateCity(new UpdateCityJson(sicity, CityFieldToUpdateEnum.BUILD));
        assertFalseResult(result);
    }

    @Test
    public void tech() {
        sicity.build = UnitType.ZEPPELIN;
        Result<SICityUpdate> result = stratInitController.updateCity(new UpdateCityJson(sicity, CityFieldToUpdateEnum.BUILD));
        assertTrue(result.isSuccess());
        assertEquals(UnitType.ZEPPELIN, result.getValue().build);
        assertEquals(CityType.TECH, result.getValue().type);
    }

    @Test
    public void noWater() {
        sicity.build = UnitType.PATROL;
        Result<SICityUpdate> result = stratInitController.updateCity(new UpdateCityJson(sicity, CityFieldToUpdateEnum.BUILD));
        assertFalseResult(result);
    }

    @Test
    public void insufficientTech() {
        sicity.build = UnitType.TANK;
        Result<SICityUpdate> result = stratInitController.updateCity(new UpdateCityJson(sicity, CityFieldToUpdateEnum.BUILD));
        assertFalseResult(result);
    }

    @Test
    public void fort() {
        sicity.build = UnitType.ZEPPELIN;
        Result<SICityUpdate> result = stratInitController.updateCity(new UpdateCityJson(sicity, CityFieldToUpdateEnum.BUILD));
        assertTrue(result.isSuccess());
        assertEquals(UnitType.ZEPPELIN, result.getValue().build);
        assertEquals(CityType.TECH, result.getValue().type);

        sicity.build = UnitType.INFANTRY;
        result = stratInitController.updateCity(new UpdateCityJson(sicity, CityFieldToUpdateEnum.BUILD));
        assertTrue(result.isSuccess());
        assertEquals(UnitType.INFANTRY, result.getValue().build);
        assertEquals(CityType.FORT, result.getValue().type);
    }

}
