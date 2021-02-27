package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.server.event.svc.EventQueue;
import com.kenstevens.stratinit.server.rest.BaseStratInitControllerTest;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CityBuilderServiceTest extends BaseStratInitControllerTest {
    protected static final SectorCoords INF_CITY = new SectorCoords(1, 4);
    private final Mockery context = new Mockery();
    private final Date now = new Date();
    private final Date started = new Date(now.getTime() - 1);
    private EventQueue eventQueue;
    private UnitDaoService unitDaoService;
    @Autowired
    private EventQueue origEventQueue;
    @Autowired
    private CityBuilderService cityBuilderService;
    @Autowired
    private UnitDaoService origUnitDaoService;
    private City city;

    @BeforeEach
    public void setupMocks() {
        eventQueue = context.mock(EventQueue.class);
        unitDaoService = context.mock(UnitDaoService.class);
        ReflectionTestUtils.setField(cityBuilderService, "eventQueue",
                eventQueue);
        ReflectionTestUtils.setField(cityBuilderService, "unitDaoService",
                unitDaoService);

        joinGamePlayerMe();
        city = sectorDao.getCity(nationMe, INF_CITY);
        city.setLastUpdated(started);
    }

    @AfterEach
    public void undoMocks() {
        ReflectionTestUtils.setField(cityBuilderService, "eventQueue",
                origEventQueue);
        ReflectionTestUtils.setField(cityBuilderService, "unitDaoService",
                origUnitDaoService);
    }

    @Test
    public void citySameBuild() {
        cityBuilderService.updateBuild(nationMe, city, UnitType.INFANTRY);
        context.assertIsSatisfied();
    }

    @Test
    public void cityNullBuild() {
        cityBuilderService.updateBuild(nationMe, city, null);
        context.assertIsSatisfied();
    }

    @Test
    public void cityNewBuild() {
        context.checking(new Expectations() {
            {
                oneOf(unitDaoService).getPower(nationMe);
                oneOf(eventQueue).cancel(city);
                oneOf(eventQueue).schedule(city);
            }
        });
        cityBuilderService.updateBuild(nationMe, city, UnitType.ZEPPELIN);
        assertFalse(city.getLastUpdated().equals(started));
        context.assertIsSatisfied();
    }

    @Test
    public void cityBaseBuild() {
        context.checking(new Expectations() {
            {
                oneOf(eventQueue).cancel(city);
            }
        });
        cityBuilderService.updateBuild(nationMe, city, UnitType.BASE);
        assertFalse(city.getLastUpdated().equals(started));
        context.assertIsSatisfied();
    }

    @Test
    public void cityResearchBuild() {
        context.checking(new Expectations() {
            {
                oneOf(eventQueue).cancel(city);
            }
        });
        cityBuilderService.updateBuild(nationMe, city, UnitType.RESEARCH);
        assertFalse(city.getLastUpdated().equals(started));
        context.assertIsSatisfied();
    }

    @Test
    public void cityCapturedBuildChange() {
        context.checking(new Expectations() {
            {
                oneOf(eventQueue).cancel(city);
            }
        });
        cityBuilderService.cityCapturedBuildChange(city);
        assertFalse(city.getLastUpdated().equals(started));
        context.assertIsSatisfied();
    }

    @Test
    public void cityBuildUnit() {
        context.checking(new Expectations() {
            {
                oneOf(unitDaoService).getPower(nationMe);
                oneOf(unitDaoService).buildUnit(nationMe, city.getCoords(), UnitType.INFANTRY, now);
            }
        });
        cityBuilderService.buildUnit(city, now);
        context.assertIsSatisfied();
    }

    @Test
    public void cityBuildNextBase() {
        context.checking(new Expectations() {
            {
                oneOf(unitDaoService).getPower(nationMe);
                oneOf(unitDaoService).buildUnit(nationMe, city.getCoords(), UnitType.INFANTRY, now);
                oneOf(eventQueue).cancel(city);
            }
        });
        city.setNextBuild(UnitType.BASE);
        cityBuilderService.buildUnit(city, now);
        context.assertIsSatisfied();
    }

    @Test
    public void cityBuildNextResearch() {
        context.checking(new Expectations() {
            {
                oneOf(unitDaoService).getPower(nationMe);
                oneOf(unitDaoService).buildUnit(nationMe, city.getCoords(), UnitType.INFANTRY, now);
                oneOf(eventQueue).cancel(city);
            }
        });
        city.setNextBuild(UnitType.RESEARCH);
        cityBuilderService.buildUnit(city, now);
        context.assertIsSatisfied();
    }

    @Test
    public void cityBuildNextZep() {
        context.checking(new Expectations() {
            {
                oneOf(unitDaoService).getPower(nationMe);
                oneOf(unitDaoService).buildUnit(nationMe, city.getCoords(), UnitType.INFANTRY, now);
                oneOf(eventQueue).cancel(city);
                oneOf(eventQueue).schedule(city);
            }
        });
        city.setNextBuild(UnitType.ZEPPELIN);
        cityBuilderService.buildUnit(city, now);
        context.assertIsSatisfied();
    }


    @Test
    public void cityBuildUnitSOTG() {
        context.checking(new Expectations() {
            {
                oneOf(unitDaoService).getPower(nationMe);
                oneOf(unitDaoService).buildUnit(nationMe, city.getCoords(), UnitType.INFANTRY, now);
            }
        });

        city.setSwitchOnTechChange(true);
        cityBuilderService.buildUnit(city, now);
        assertTrue(city.isSwitchOnTechChange());
        context.assertIsSatisfied();
    }

    @Test
    public void cityBuildNextBaseSOTG() {
        context.checking(new Expectations() {
            {
                oneOf(unitDaoService).getPower(nationMe);
                oneOf(unitDaoService).buildUnit(nationMe, city.getCoords(), UnitType.INFANTRY, now);
                oneOf(eventQueue).cancel(city);
            }
        });

        city.setSwitchOnTechChange(true);
        city.setNextBuild(UnitType.BASE);
        cityBuilderService.buildUnit(city, now);
        assertFalse(city.isSwitchOnTechChange());
        context.assertIsSatisfied();
    }

    @Test
    public void cityBuildNextResearchSOTG() {
        context.checking(new Expectations() {
            {
                oneOf(unitDaoService).getPower(nationMe);
                oneOf(unitDaoService).buildUnit(nationMe, city.getCoords(), UnitType.INFANTRY, now);
                oneOf(eventQueue).cancel(city);
            }
        });

        city.setSwitchOnTechChange(true);
        city.setNextBuild(UnitType.RESEARCH);
        cityBuilderService.buildUnit(city, now);
        assertFalse(city.isSwitchOnTechChange());
        context.assertIsSatisfied();
    }

    @Test
    public void cityBuildNextZepSOTG() {
        context.checking(new Expectations() {
            {
                oneOf(unitDaoService).getPower(nationMe);
                oneOf(unitDaoService).buildUnit(nationMe, city.getCoords(), UnitType.INFANTRY, now);
                oneOf(eventQueue).cancel(city);
                oneOf(eventQueue).schedule(city);
            }
        });

        city.setSwitchOnTechChange(true);
        city.setNextBuild(UnitType.ZEPPELIN);
        cityBuilderService.buildUnit(city, now);
        assertFalse(city.isSwitchOnTechChange());
        context.assertIsSatisfied();
    }

    @Test
    public void switchOnTechGain() {
        cityBuilderService.switchCityProductionIfTechPermits(city, now);
        context.assertIsSatisfied();
    }

    @Test
    public void switchOnTechGainBase() {
        context.checking(new Expectations() {
            {
                oneOf(eventQueue).cancel(city);
                oneOf(eventQueue).schedule(city);
            }
        });
        city.setNextBuild(UnitType.ZEPPELIN);
        cityBuilderService.switchCityProductionIfTechPermits(city, now);
        assertFalse(city.getLastUpdated().equals(started));

        context.assertIsSatisfied();
    }

    @Test
    public void switchOnTechGainBaseSOTG() {
        context.checking(new Expectations() {
            {
                oneOf(eventQueue).cancel(city);
                oneOf(eventQueue).schedule(city);
            }
        });
        city.setSwitchOnTechChange(true);
        city.setNextBuild(UnitType.ZEPPELIN);
        cityBuilderService.switchCityProductionIfTechPermits(city, now);
        assertFalse(city.getLastUpdated().equals(started));
        assertFalse(city.isSwitchOnTechChange());

        context.assertIsSatisfied();
    }
}
