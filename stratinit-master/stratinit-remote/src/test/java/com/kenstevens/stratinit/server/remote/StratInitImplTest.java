package com.kenstevens.stratinit.server.remote;

import com.kenstevens.stratinit.dto.SIUnitBase;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StratInitImplTest extends StratInitDaoBase {
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
        assertEquals("" + Constants.MAX_PLAYERS_PER_GAME, props.get("MAX_PLAYERS_PER_GAME"));
    }
}
