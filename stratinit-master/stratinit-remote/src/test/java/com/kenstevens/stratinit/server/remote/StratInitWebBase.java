package com.kenstevens.stratinit.server.remote;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;
import com.kenstevens.stratinit.server.daoservice.MoveService;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class StratInitWebBase extends StratInitDaoBase {
	@Autowired
	protected GameDaoService gameDaoService;
	@Autowired
	protected SectorDaoService sectorDaoService;
	@Autowired
	protected UnitDaoService unitDaoService;
	@Autowired
	private MoveService moveService;
	@Autowired
	@Qualifier("stratInit")
	protected StratInit stratInit;

	protected Result<Nation> joinGamePlayerMe() {
		Result<Nation> retval = joinGame(playerMe);
		nationMeId = retval.getValue().getNationId();
		nationMe = gameDao.findNation(testGameId, playerMe);
		setAuthentication(PLAYER_ME_NAME);
		return retval;
	}

	protected void setBuild(SectorCoords coords, UnitType type) {
		City city = sectorDao.getCity(testWorld.getSector(coords));
		sectorDaoService.updateCity(city.getNation(), coords, UpdateCityField.BUILD, type, null, false, null);
	}

	protected void setAuthentication(String username) {
		new AuthenticationHelper().setAuthentication(username);
		stratInit.setGame(testGameId, false);
	}

	protected Result<Nation> joinGame(Player player) {
		Result<Nation> result = stratInit.joinGame(player, testGameId, false);
		assertResult(result);
		return result;
	}

	protected Result<MoveCost> moveUnits(List<SIUnit> units, SectorCoords targetCoords) {
		return moveService.move(nationMe, units, targetCoords);
	}

	protected Result<MoveCost> moveUnits(Nation nation, List<SIUnit> units, SectorCoords targetCoords) {
		return moveService.move(nation, units, targetCoords);
	}
	
	protected void assertNotFired(Result<MoveCost> result, Unit itank) {
		assertEquals(itank.getUnitBase().getAmmo(),
				itank.getAmmo(), result.toString());
	}

	protected <T> void assertNotDamaged(Result<T> result, Unit itank) {
		assertEquals(itank.getUnitBase().getHp(),
				itank.getHp(), result.toString());
	}

	protected <T> void assertDamaged(Result<T> result, Unit itank) {
		assertTrue(
				itank.getUnitBase().getHp() > itank.getHp(), result.toString());
	}

	protected void assertNotMoved(Result<MoveCost> result, Unit itank) {
		assertEquals(itank.getUnitBase().getMobility(),
				itank.getMobility(), result.toString());
	}

	protected void assertMoved(Result<MoveCost> result, Unit itank) {
		assertTrue(itank.getUnitBase().getMobility() > itank.getMobility(),
				result.toString());
	}

	protected void assertFired(Result<MoveCost> result, Unit itank) {
		assertTrue(itank.getUnitBase().getAmmo() > itank.getAmmo(),
				result.toString());
	}

	protected void assertShortFuel(Result<MoveCost> result, Unit plane) {
		assertTrue(plane.getUnitBase().getMaxFuel() > plane.getFuel(),
				result.toString());
	}

	protected void assertFullFuel(Result<MoveCost> result, Unit plane) {
		assertEquals(
				plane.getUnitBase().getMaxFuel(), plane.getFuel(), result.toString());
	}

	protected void assertFiredOnce(Result<MoveCost> result, Unit itank) {
		assertEquals(
				itank.getUnitBase().getAmmo() - 1, itank.getAmmo(), result.toString());
	}

	protected int getCapacity(UnitType unitType) {
		return UnitBase.getUnitBase(unitType).getCapacity();
	}
}
