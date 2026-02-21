package com.kenstevens.stratinit;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.client.server.rest.AuthenticationHelper;
import com.kenstevens.stratinit.client.server.rest.StratInitDaoBase;
import com.kenstevens.stratinit.controller.*;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.helper.PlayerHelper;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.SetGameJson;
import com.kenstevens.stratinit.server.service.*;
import com.kenstevens.stratinit.server.svc.MoveService;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class BaseStratInitControllerTest extends StratInitDaoBase {
	@Autowired
	protected GameService gameService;
	@Autowired
	protected RelationService relationService;
	@Autowired
	protected SectorService sectorService;
	@Autowired
	protected CityService cityService;
	@Autowired
	protected UnitService unitService;
	@Autowired
	protected GameController gameController;
	@Autowired
	protected UnitController unitController;
	@Autowired
	protected CityController cityController;
	@Autowired
	protected NationController nationController;
	@Autowired
	protected MessageController messageController;
	@Autowired
	private MoveService moveService;

	protected Result<SINation> joinGamePlayerMe() {
		Result<SINation> retval = joinGame(playerMe);
		nationMe = nationDao.findNation(testGameId, playerMe);
		setAuthentication(PlayerHelper.PLAYER_ME);
		return retval;
	}

    protected void setBuild(SectorCoords coords, UnitType type) {
        City city = cityDao.getCity(testWorld.getSectorOrNull(coords));
		cityService.updateCity(city.getNation(), coords, CityFieldToUpdateEnum.BUILD, type, null, false, null);
    }

    protected void setAuthentication(String username) {
		new AuthenticationHelper().setAuthentication(username);
		gameController.setGame(new SetGameJson(testGameId, false));
    }

    protected Result<SINation> joinGame(Player player) {
		setAuthentication(player.getUsername());
		Result<SINation> result = gameController.joinGame(new SetGameJson(testGameId, false));
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
