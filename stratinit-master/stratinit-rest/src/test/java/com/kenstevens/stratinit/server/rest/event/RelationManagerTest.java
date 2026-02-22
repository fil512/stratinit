package com.kenstevens.stratinit.server.rest.event;

import com.kenstevens.stratinit.client.model.Relation;
import com.kenstevens.stratinit.server.event.svc.EventQueue;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.RelationType;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public abstract class RelationManagerTest extends TwoPlayerBase {
    @Autowired
    private EventQueue eventQueue;

    protected void changedTo(RelationType nextType) {
        setRelation(nationThemId, nextType);
        Relation relation = relationDao.findRelation(nationMe, nationThem);
        assertEquals(nextType, relation.getType());
        assertNull(relation.getNextType());
        assertNull(relation.getSwitchTime());
        assertFalse(eventQueue.cancel(relation));
    }

    protected void changedToDelayed(RelationType nextType) {
        Relation relation = relationDao.findRelation(nationMe, nationThem);
        RelationType pre = relation.getType();
        setRelation(nationThemId, nextType);
        relation = relationDao.findRelation(nationMe, nationThem);
        assertEquals(pre, relation.getType());
        assertEquals(nextType, relation.getNextType());
        assertNotNull(relation.getSwitchTime());
        assertTrue(eventQueue.cancel(relation));
    }
}
