package com.kenstevens.stratinit.server.remote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.main.Spring;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;
import com.kenstevens.stratinit.server.daoservice.MoveService;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.remote.mail.SMTPService;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

@Ignore
public class StratInitWebBase extends StratInitDaoBase {
	@Autowired protected GameDaoService gameDaoService;
	@Autowired protected SectorDaoService sectorDaoService;
	@Autowired protected UnitDaoService unitDaoService;
	@Autowired protected Spring spring;
	@Autowired
	private SMTPService smtpService;
	@Autowired
	private MoveService moveService;

	protected StratInit stratInit;

	@Before
	public void setup() {
		stratInit = spring.getBean("stratInit", StratInit.class);
		smtpService.disable();
	}


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
		assertEquals(result.toString(), itank.getUnitBase().getAmmo(),
				itank.getAmmo());
	}

	protected <T> void assertNotDamaged(Result<T> result, Unit itank) {
		assertEquals(result.toString(), itank.getUnitBase().getHp(),
				itank.getHp());
	}

	protected <T> void assertDamaged(Result<T> result, Unit itank) {
		assertTrue(result.toString(),
				itank.getUnitBase().getHp() > itank.getHp());
	}

	protected void assertNotMoved(Result<MoveCost> result, Unit itank) {
		assertEquals(result.toString(), itank.getUnitBase().getMobility(),
				itank.getMobility());
	}

	protected void assertMoved(Result<MoveCost> result, Unit itank) {
		assertTrue(result.toString(),
				itank.getUnitBase().getMobility() > itank.getMobility());
	}

	protected void assertFired(Result<MoveCost> result, Unit itank) {
		assertTrue(result.toString(),
				itank.getUnitBase().getAmmo() > itank.getAmmo());
	}

	protected void assertShortFuel(Result<MoveCost> result, Unit plane) {
		assertTrue(result.toString(),
				plane.getUnitBase().getMaxFuel() > plane.getFuel());
	}

	protected void assertFullFuel(Result<MoveCost> result, Unit plane) {
		assertEquals(result.toString(),
				plane.getUnitBase().getMaxFuel(), plane.getFuel());
	}

	protected void assertFiredOnce(Result<MoveCost> result, Unit itank) {
		assertEquals(result.toString(),
				itank.getUnitBase().getAmmo() - 1, itank.getAmmo());
	}

	protected int getCapacity(UnitType unitType) {
		return UnitBase.getUnitBase(unitType).getCapacity();
	}
}
