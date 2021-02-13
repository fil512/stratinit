package com.kenstevens.stratinit.server.remote;

import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public abstract class WithUnitsBase extends StratInitWebBase {
	@Autowired
	protected UnitDaoService unitDaoServiceImpl;

	protected static final SectorCoords START_COORDS = new SectorCoords(0,0);
	protected static final SectorCoords SEA1 = new SectorCoords(3,0);
	protected static final SectorCoords SEA2 = new SectorCoords(3,1);
	protected Unit testInfantry;
	protected int testInfantryId;
	protected Unit testHelicopter;
	protected int testHelicopterId;
	protected List<Unit>testInfantries = new ArrayList<Unit>();

	public WithUnitsBase() {
		super();
	}

	@Before
	public void doJoinGame() {
        joinGamePlayerMe();
        testInfantry = unitDaoServiceImpl.buildUnit(nationMe, START_COORDS, UnitType.INFANTRY);
        testInfantry.addMobility();
        testInfantry.addMobility();
        testInfantryId = testInfantry.getId();
        testHelicopter = unitDaoServiceImpl.buildUnit(nationMe, START_COORDS, UnitType.HELICOPTER);
        unitDao.save(testHelicopter);
        testHelicopterId = testHelicopter.getId();
        unitDaoServiceImpl.buildUnit(nationMe, SEA1, UnitType.TRANSPORT);

        unitDaoServiceImpl.buildUnit(nationMe, SEA2, UnitType.TRANSPORT);
        for (int i = 0; i < 6; ++i) {
            testInfantries.add(unitDaoServiceImpl.buildUnit(nationMe, SEA2, UnitType.INFANTRY));
        }
    }

}