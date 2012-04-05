package com.kenstevens.stratinit.server.daoservice;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.server.event.EventQueue;
import com.kenstevens.stratinit.server.remote.StratInitWebBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class CityBuilderServiceTest extends StratInitWebBase {
	private Mockery context = new Mockery();

	private EventQueue eventQueue;
	private UnitDaoService unitDaoService;
	
	protected static final SectorCoords INF_CITY = new SectorCoords(1,4);

	@Autowired
	private EventQueue origEventQueue;
	@Autowired
	private CityBuilderService cityBuilderService;
	@Autowired
	private UnitDaoService origUnitDaoService;
	
	private Date now = new Date();
	private Date started = new Date(now.getTime() - 1);
	private City city;
	
	@Before
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

	@After
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
