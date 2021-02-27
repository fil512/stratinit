package com.kenstevens.stratinit.server.rest.commands;

import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetNationsTest extends TwoPlayerBase {
    @Test
    public void getNations() {
        List<SINation> nations = stratInitController.getNations().getValue();
        assertEquals(2, nations.size());
        SINation nationMe = nations.get(0);

        assertEquals(0, nationMe.nationId);
        assertEquals(PLAYER_ME_NAME, nationMe.name);
        assertEquals(0.0, nationMe.tech, 0.001);

        SINation nationThem = nations.get(1);
        assertEquals(PLAYER_THEM_NAME, nationThem.name);
        assertEquals(SINation.UNKNOWN, nationThem.tech, 0.001);
        assertEquals(1, nationThem.nationId);
    }


}
