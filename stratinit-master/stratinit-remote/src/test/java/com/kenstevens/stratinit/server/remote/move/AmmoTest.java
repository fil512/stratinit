package com.kenstevens.stratinit.server.remote.move;

import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.remote.BaseStratInitWebTest;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AmmoTest extends BaseStratInitWebTest {
    private static final SectorCoords FAR_PORT = new SectorCoords(6, 2);
    private static final SectorCoords CLOSE_PORT = new SectorCoords(6, 7);
    private static final SectorCoords NEAR_PORT = new SectorCoords(6, 8);
    private static final SectorCoords PORT = new SectorCoords(7, 8);

    @Autowired
    protected SectorDaoService sectorDaoServiceImpl;

    @BeforeEach
    public void doJoinGame() {
		joinGamePlayerMe();
	}


	@Test
	public void shipMoveIntoSupplyToGetAmmo() {
		Unit dest = unitDaoService.buildUnit(nationMe, FAR_PORT,
				UnitType.DESTROYER);
		dest.setMobility(UnitBase.getUnitBase(UnitType.DESTROYER).getMaxMobility());
		dest.decreaseAmmo();
		assertEquals(dest.getUnitBase().getAmmo() - 1, dest.getAmmo());
		sectorDaoServiceImpl.captureCity(nationMe, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest), NEAR_PORT);
		assertResult(result);
		assertNotFired(result, dest);
	}

	@Test
	public void captureCityToGetAmmo() {
		Unit dest = unitDaoService.buildUnit(nationMe, NEAR_PORT,
				UnitType.DESTROYER);
		dest.decreaseAmmo();
		assertEquals(dest.getUnitBase().getAmmo() - 1, dest.getAmmo());
		sectorDaoServiceImpl.captureCity(nationMe, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		assertEquals(dest.getUnitBase().getAmmo(), dest.getAmmo());
	}

	@Test
	public void moveSupplyToGetAmmo() {
		Unit dest = unitDaoService.buildUnit(nationMe, FAR_PORT,
				UnitType.DESTROYER);
		dest.decreaseAmmo();
		Unit supply = unitDaoService.buildUnit(nationMe, NEAR_PORT,
				UnitType.SUPPLY);
		Result<MoveCost> result = moveUnits(
				makeUnitList(supply), CLOSE_PORT);
		assertResult(result);
		assertNotFired(result, dest);
	}
}
