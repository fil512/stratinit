package com.kenstevens.stratinit.server.remote.request.write;

import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitSeen;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.server.remote.request.RequestFactory;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CedeUnitTest extends TwoPlayerBase {
    private static final SectorCoords BESIDE_MY_CITY = new SectorCoords(1, 3);
    private static final SectorCoords MY_CITY = new SectorCoords(1, 4);
    private static final SectorCoords AT_SEA = new SectorCoords(5, 4);

    @Autowired
    private RequestFactory requestFactory;

    @Test
    public void cededCanSee() {
        declareAlliance();
        allianceDeclared();
        Unit inf = unitDaoService.buildUnit(nationMe, BESIDE_MY_CITY, UnitType.INFANTRY);
        Unit inf2 = unitDaoService.buildUnit(nationMe, MY_CITY, UnitType.INFANTRY);
        UnitSeen unitSeen = unitDao.findUnitSeen(nationThem, inf2);
        assertNull(unitSeen);
        CedeUnitsRequest cedeUnitsRequest = requestFactory.getCedeUnitsRequest(makeUnitList(inf), nationThemId);
        Result<SIUpdate> result = cedeUnitsRequest.executeWrite();
        assertResult(result);
        unitSeen = unitDao.findUnitSeen(nationThem, inf2);
        assertNotNull(unitSeen);
    }

    @Test
    public void cedeTwoUnits() {
        declareAlliance();
        allianceDeclared();
        Unit inf = unitDaoService.buildUnit(nationMe, BESIDE_MY_CITY, UnitType.INFANTRY);
        Unit inf2 = unitDaoService.buildUnit(nationMe, BESIDE_MY_CITY, UnitType.INFANTRY);
        List<Unit> units = new ArrayList<Unit>();
        units.add(inf);
        units.add(inf2);
        CedeUnitsRequest cedeUnitsRequest = requestFactory.getCedeUnitsRequest(makeUnitList(units), nationThemId);
        Result<SIUpdate> result = cedeUnitsRequest.executeWrite();
        assertResult(result);
        inf = unitDao.findUnit(inf.getId());
        assertEquals(nationThem, inf.getNation());
        assertTrue(inf.isAlive());
        inf2 = unitDao.findUnit(inf2.getId());
        assertEquals(nationThem, inf2.getNation());
        assertTrue(inf2.isAlive());
    }

    // TODO TEST this needs to be tested with 3 players
    @Test
    public void cannotCedeWithNeutralOthers() {
        Unit inf = unitDaoService.buildUnit(nationMe, BESIDE_MY_CITY, UnitType.INFANTRY);
        unitDaoService.buildUnit(nationMe, BESIDE_MY_CITY, UnitType.INFANTRY);
        CedeUnitsRequest cedeUnitsRequest = requestFactory.getCedeUnitsRequest(makeUnitList(inf), nationThemId);
        Result<SIUpdate> result = cedeUnitsRequest.executeWrite();
        assertFalseResult(result);
    }

    @Test
    public void cannotCedeIfNeutral() {
        Unit inf = unitDaoService.buildUnit(nationMe, BESIDE_MY_CITY, UnitType.INFANTRY);
        CedeUnitsRequest cedeUnitsRequest = requestFactory.getCedeUnitsRequest(makeUnitList(inf), nationThemId);
        Result<SIUpdate> result = cedeUnitsRequest.executeWrite();
        assertFalseResult(result);
    }

    @Test
    public void cannotCedeIfFriendly() {
        declareFriendly();
        friendlyDeclared();
        Unit inf = unitDaoService.buildUnit(nationMe, BESIDE_MY_CITY, UnitType.INFANTRY);
        CedeUnitsRequest cedeUnitsRequest = requestFactory.getCedeUnitsRequest(makeUnitList(inf), nationThemId);
        Result<SIUpdate> result = cedeUnitsRequest.executeWrite();
        assertFalseResult(result);
    }

    @Test
    public void cannotCedeIfWar() {
        declareWar();
        warDeclared();
        Unit inf = unitDaoService.buildUnit(nationMe, BESIDE_MY_CITY, UnitType.INFANTRY);
        CedeUnitsRequest cedeUnitsRequest = requestFactory.getCedeUnitsRequest(makeUnitList(inf), nationThemId);
        Result<SIUpdate> result = cedeUnitsRequest.executeWrite();
        assertFalseResult(result);
    }

    @Test
    public void canCedeIfAllied() {
        declareAlliance();
        allianceDeclared();
        Unit inf = unitDaoService.buildUnit(nationMe, BESIDE_MY_CITY, UnitType.INFANTRY);
        CedeUnitsRequest cedeUnitsRequest = requestFactory.getCedeUnitsRequest(makeUnitList(inf), nationThemId);
        Result<SIUpdate> result = cedeUnitsRequest.executeWrite();
        assertResult(result);
    }

    @Test
    public void canCedeWithAllyOthers() {
        declareAlliance();
        allianceDeclared();
        Unit inf = unitDaoService.buildUnit(nationMe, BESIDE_MY_CITY, UnitType.INFANTRY);
        unitDaoService.buildUnit(nationMe, BESIDE_MY_CITY, UnitType.INFANTRY);
        CedeUnitsRequest cedeUnitsRequest = requestFactory.getCedeUnitsRequest(makeUnitList(inf), nationThemId);
        Result<SIUpdate> result = cedeUnitsRequest.executeWrite();
        assertResult(result);
    }

    @Test
    public void canCedeWithOthersAtSea() {
        declareAlliance();
        allianceDeclared();
        Unit inf = unitDaoService.buildUnit(nationMe, AT_SEA, UnitType.INFANTRY);
        Unit trans = unitDaoService.buildUnit(nationMe, AT_SEA, UnitType.TRANSPORT);
        CedeUnitsRequest cedeUnitsRequest = requestFactory.getCedeUnitsRequest(makeUnitList(trans), nationThemId);
        Result<SIUpdate> result = cedeUnitsRequest.executeWrite();
        assertResult(result);
        inf = unitDao.findUnit(inf.getId());
        assertEquals(nationThem, inf.getNation());
    }

}
