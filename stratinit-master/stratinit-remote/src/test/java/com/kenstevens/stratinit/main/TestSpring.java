package com.kenstevens.stratinit.main;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kenstevens.stratinit.QuiesceService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/WEB-INF/applicationContext.xml")
public class TestSpring {
	@Autowired protected Spring spring;

	@Test
	public void canFindBeansOfTypeUnitDao() {
		Map<String, UnitDaoService> beanMap = spring.getBeansOfType(UnitDaoService.class);
		assertTrue(beanMap.containsKey("unitDaoServiceImpl"));
	}

	@Test
	public void canFindBeansOfTypeQuiesceService() {
		Map<String, QuiesceService> beanMap = spring.getBeansOfType(QuiesceService.class);
		assertTrue(beanMap.containsKey("cacheQuiescer"));
		assertTrue(beanMap.containsKey("eventQueueQuiescer"));
	}

}
