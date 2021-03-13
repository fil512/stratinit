package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.BaseStratInitControllerTest;
import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.helper.UnitHelper;
import com.kenstevens.stratinit.server.event.svc.EventQueue;
import com.kenstevens.stratinit.server.svc.CityBuilderService;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DirtiesContext
public class CityBuilderServiceTest extends BaseStratInitControllerTest {
    protected static final SectorCoords INF_CITY = new SectorCoords(1, 4);
    private final Date now = new Date();
    private final Date started = new Date(now.getTime() - 1);

    @MockBean
    private EventQueue eventQueue;
    @MockBean
    private UnitDaoService unitDaoService;

    @Autowired
    private CityBuilderService cityBuilderService;
    private City city;

    @BeforeEach
    public void init() {
        super.init();
        when(unitDaoService.buildUnit(any(), any(), any())).thenReturn(UnitHelper.meUnit);

        joinGamePlayerMe();
        city = cityDao.getCity(nationMe, INF_CITY);
        city.setLastUpdated(started);

        reset(eventQueue);
    }

    @Test
    public void citySameBuild() {
        cityBuilderService.updateBuild(nationMe, city, UnitType.INFANTRY);
    }

    @Test
    public void cityNullBuild() {
        cityBuilderService.updateBuild(nationMe, city, null);
    }

    @Test
    public void cityNewBuild() {
        cityBuilderService.updateBuild(nationMe, city, UnitType.ZEPPELIN);
        assertFalse(city.getLastUpdated().equals(started));
        verify(unitDaoService).getPower(nationMe);
        verify(eventQueue).cancel(city);
        verify(eventQueue).schedule(city);
    }

    @Test
    public void cityBaseBuild() {
        cityBuilderService.updateBuild(nationMe, city, UnitType.BASE);
        assertFalse(city.getLastUpdated().equals(started));
        verify(eventQueue).cancel(city);
    }

    @Test
    public void cityResearchBuild() {
        cityBuilderService.updateBuild(nationMe, city, UnitType.RESEARCH);
        assertFalse(city.getLastUpdated().equals(started));
        verify(eventQueue).cancel(city);
    }

    @Test
    public void cityCapturedBuildChange() {
        cityBuilderService.cityCapturedBuildChange(city);
        assertFalse(city.getLastUpdated().equals(started));
        verify(eventQueue).cancel(city);
    }

    @Test
    public void cityBuildUnit() {
        cityBuilderService.buildUnit(city, now);
        verify(unitDaoService).getPower(nationMe);
        verify(unitDaoService).buildUnit(nationMe, city.getCoords(), UnitType.INFANTRY, now);
    }

    @Test
    public void cityBuildNextBase() {
        city.setNextBuild(UnitType.BASE);
        cityBuilderService.buildUnit(city, now);
        verify(unitDaoService).getPower(nationMe);
        verify(unitDaoService).buildUnit(nationMe, city.getCoords(), UnitType.INFANTRY, now);
        verify(eventQueue).cancel(city);
    }

    @Test
    public void cityBuildNextResearch() {
        city.setNextBuild(UnitType.RESEARCH);
        cityBuilderService.buildUnit(city, now);
        verify(unitDaoService).getPower(nationMe);
        verify(unitDaoService).buildUnit(nationMe, city.getCoords(), UnitType.INFANTRY, now);
        verify(eventQueue).cancel(city);
    }

    @Test
    public void cityBuildNextZep() {
        city.setNextBuild(UnitType.ZEPPELIN);
        cityBuilderService.buildUnit(city, now);
        verify(unitDaoService).getPower(nationMe);
        verify(unitDaoService).buildUnit(nationMe, city.getCoords(), UnitType.INFANTRY, now);
        verify(eventQueue).cancel(city);
        verify(eventQueue).schedule(city);
    }


    @Test
    public void cityBuildUnitSOTG() {
        city.setSwitchOnTechChange(true);
        cityBuilderService.buildUnit(city, now);
        assertTrue(city.isSwitchOnTechChange());
        verify(unitDaoService).getPower(nationMe);
        verify(unitDaoService).buildUnit(nationMe, city.getCoords(), UnitType.INFANTRY, now);
    }

    @Test
    public void cityBuildNextBaseSOTG() {
        city.setSwitchOnTechChange(true);
        city.setNextBuild(UnitType.BASE);
        cityBuilderService.buildUnit(city, now);
        assertFalse(city.isSwitchOnTechChange());
        verify(unitDaoService).getPower(nationMe);
        verify(unitDaoService).buildUnit(nationMe, city.getCoords(), UnitType.INFANTRY, now);
        verify(eventQueue).cancel(city);
    }

    @Test
    public void cityBuildNextResearchSOTG() {
        city.setSwitchOnTechChange(true);
        city.setNextBuild(UnitType.RESEARCH);
        cityBuilderService.buildUnit(city, now);
        assertFalse(city.isSwitchOnTechChange());
        verify(unitDaoService).getPower(nationMe);
        verify(unitDaoService).buildUnit(nationMe, city.getCoords(), UnitType.INFANTRY, now);
        verify(eventQueue).cancel(city);
    }

    @Test
    public void cityBuildNextZepSOTG() {
        city.setSwitchOnTechChange(true);
        city.setNextBuild(UnitType.ZEPPELIN);
        cityBuilderService.buildUnit(city, now);
        assertFalse(city.isSwitchOnTechChange());
        verify(unitDaoService).getPower(nationMe);
        verify(unitDaoService).buildUnit(nationMe, city.getCoords(), UnitType.INFANTRY, now);
        verify(eventQueue).cancel(city);
        verify(eventQueue).schedule(city);
    }

    @Test
    public void switchOnTechGain() {
        cityBuilderService.switchCityProductionIfTechPermits(city, now);
    }

    @Test
    public void switchOnTechGainBase() {
        city.setNextBuild(UnitType.ZEPPELIN);
        cityBuilderService.switchCityProductionIfTechPermits(city, now);
        assertFalse(city.getLastUpdated().equals(started));
        verify(eventQueue).cancel(city);
        verify(eventQueue).schedule(city);
    }

    @Test
    public void switchOnTechGainBaseSOTG() {
        city.setSwitchOnTechChange(true);
        city.setNextBuild(UnitType.ZEPPELIN);
        cityBuilderService.switchCityProductionIfTechPermits(city, now);
        assertFalse(city.getLastUpdated().equals(started));
        assertFalse(city.isSwitchOnTechChange());
        verify(eventQueue).cancel(city);
        verify(eventQueue).schedule(city);
    }
}
