package com.kenstevens.stratinit.server.rest.commands;

import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.RelationType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GetRelationsTest extends TwoPlayerBase {
    private List<SIRelation> relations;

    @Test
    public void nation2() {
        relations = stratInitController.getRelations().getValue();
        SIRelation sirelation = getRelation(nationThemId);
        assertEquals(RelationType.NEUTRAL, sirelation.meToThem);
        assertEquals(RelationType.NEUTRAL, sirelation.themToMe);
        assertNull(sirelation.myNextType);
        assertNull(sirelation.mineSwitches);
    }

    @Test
    public void nation2war() {
        declareWar();
        relations = stratInitController.getRelations().getValue();
        SIRelation sirelation = getRelation(nationThemId);
        assertEquals(RelationType.WAR, sirelation.themToMe);
        assertEquals(RelationType.WAR, sirelation.meToThem);
        assertNull(sirelation.myNextType);
        assertNull(sirelation.mineSwitches);
    }

    @Test
    public void nation2warBack() {
        warDeclared();
        relations = stratInitController.getRelations().getValue();
        SIRelation sirelation = getRelation(nationThemId);
        assertEquals(RelationType.NEUTRAL, sirelation.meToThem);
        assertEquals(RelationType.WAR, sirelation.themToMe);
        assertNull(sirelation.myNextType);
        assertNull(sirelation.mineSwitches);
    }

    @Test
    public void nation1() {
        relations = stratInitController.getRelations().getValue();
        SIRelation sirelation = getRelation(nationMeId);
        assertEquals(RelationType.ME, sirelation.meToThem);
        assertEquals(RelationType.ME, sirelation.themToMe);
        assertNull(sirelation.myNextType);
        assertNull(sirelation.mineSwitches);
    }

    private SIRelation getRelation(int nationId) {
        for (SIRelation relation : relations) {
            if (relation.nationId == nationId) {
                return relation;
            }
        }
        return null;
    }

}
