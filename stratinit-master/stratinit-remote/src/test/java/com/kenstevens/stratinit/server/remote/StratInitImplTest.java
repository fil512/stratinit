package com.kenstevens.stratinit.server.remote;

import com.kenstevens.stratinit.DaoConfig;
import com.kenstevens.stratinit.dto.SIUnitBase;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DaoConfig.class})
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
