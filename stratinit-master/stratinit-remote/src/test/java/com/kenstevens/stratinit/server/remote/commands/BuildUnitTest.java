package com.kenstevens.stratinit.server.remote.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.server.remote.StratInitWebBase;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.UnitType;

public class BuildUnitTest extends StratInitWebBase {
	private SICity sicity;

	@Before
	public void doJoinGame() throws InterruptedException {
		joinGamePlayerMe();
		List<SICity> cities = stratInit.getCities().getValue();
		sicity = cities.get(0);
	}

	@Test
	public void airport() {
		sicity.build = UnitType.FIGHTER;
		Result<SICity> result = stratInit.updateCity(sicity, UpdateCityField.BUILD);
		assertFalseResult(result);
	}

	@Test
	public void tech() {
		sicity.build = UnitType.ZEPPELIN;
		Result<SICity> result = stratInit.updateCity(sicity, UpdateCityField.BUILD);
		assertTrue(result.isSuccess());
		assertEquals(UnitType.ZEPPELIN, result.getValue().build);
		assertEquals(CityType.TECH, result.getValue().type);
	}

	@Test
	public void noWater() {
		sicity.build = UnitType.PATROL;
		Result<SICity> result = stratInit.updateCity(sicity, UpdateCityField.BUILD);
		assertFalseResult(result);
	}

	@Test
	public void insufficientTech() {
		sicity.build = UnitType.TANK;
		Result<SICity> result = stratInit.updateCity(sicity, UpdateCityField.BUILD);
		assertFalseResult(result);
	}

	@Test
	public void fort() {
		sicity.build = UnitType.ZEPPELIN;
		Result<SICity> result = stratInit.updateCity(sicity, UpdateCityField.BUILD);
		assertTrue(result.isSuccess());
		assertEquals(UnitType.ZEPPELIN, result.getValue().build);
		assertEquals(CityType.TECH, result.getValue().type);

		sicity.build = UnitType.INFANTRY;
		result = stratInit.updateCity(sicity, UpdateCityField.BUILD);
		assertTrue(result.isSuccess());
		assertEquals(UnitType.INFANTRY, result.getValue().build);
		assertEquals(CityType.FORT, result.getValue().type);
	}

}
