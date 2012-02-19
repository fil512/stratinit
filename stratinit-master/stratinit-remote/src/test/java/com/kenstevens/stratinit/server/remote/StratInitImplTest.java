package com.kenstevens.stratinit.server.remote;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kenstevens.stratinit.dto.SIUnitBase;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.UnitType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/WEB-INF/applicationContext.xml")
public class StratInitImplTest {
	@Autowired
	private StratInit stratInit;
	
	@Test
	public void getUnitBases() {
		Result<List<SIUnitBase>> result = stratInit.getUnitBases();
		List<SIUnitBase> unitBases = result.getValue();
		assertEquals(UnitType.INFANTRY, unitBases.get(0).type);
	}
	
	@Test
	public void getServerConfig() {
		Result<Properties> result = stratInit.getServerConfig();
		Properties props = result.getValue();
		assertEquals(""+Constants.MAX_PLAYERS_PER_GAME, props.get("MAX_PLAYERS_PER_GAME"));
	}
}
