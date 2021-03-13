package com.kenstevens.stratinit.server.rest.event;

import com.kenstevens.stratinit.client.model.Relation;
import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.event.svc.EventQueue;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.RelationType;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

// FIXME enable
@Disabled
public class RelationManagerTest extends TwoPlayerBase {
    @Autowired
    private EventQueue eventQueue;

    protected void changedTo(RelationType nextType) {
        Result<SIRelation> result = setRelation(nationThemId, nextType);
        assertResult(result);
        Relation relation = relationDao.findRelation(nationMe, nationThem);
        assertEquals(nextType, relation.getType(), result.toString());
        assertNull(relation.getNextType());
        assertNull(relation.getSwitchTime());
        assertFalse(eventQueue.cancel(relation));
    }

    protected void changedToDelayed(RelationType nextType) {
        Relation relation = relationDao.findRelation(nationMe, nationThem);
        RelationType pre = relation.getType();
        Result<SIRelation> result = setRelation(nationThemId, nextType);
        assertResult(result);
        relation = relationDao.findRelation(nationMe, nationThem);
        assertEquals(pre, relation.getType(), result.toString());
        assertEquals(nextType, relation.getNextType(), result.toString());
        assertNotNull(relation.getSwitchTime());
        assertTrue(eventQueue.cancel(relation));
    }
}
