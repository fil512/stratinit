package com.kenstevens.stratinit.server.rest.event;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Relation;
import com.kenstevens.stratinit.server.event.svc.EventQueue;
import com.kenstevens.stratinit.server.rest.ThreePlayerBase;
import com.kenstevens.stratinit.type.RelationType;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public abstract class ThreeRelationManagerTest extends ThreePlayerBase {
    @Autowired
    private EventQueue eventQueue;


    // TODO REF repeated with TwoPlayerBase
    protected void changedTo(RelationType nextType) {
        setRelation(nationThemId, nextType);
        assertRelationChanged(nationThem, nextType);
    }

    protected void assertRelationChanged(Nation nation, RelationType nextType) {
        assertRelationChanged(nationMe, nation, nextType);
    }

    protected void assertRelationChanged(Nation nationFrom, Nation nationTo, RelationType nextType) {
        Relation relation = relationDao.findRelation(nationFrom, nationTo);
        assertEquals(nextType, relation.getType());
        assertNull(relation.getNextType());
        assertNull(relation.getSwitchTime());
        assertFalse(eventQueue.cancel(relation));
    }

    // TODO REF repeated with TwoPlayerBase
    protected void changedToDelayed(RelationType nextType) {
        Relation relation = relationDao.findRelation(nationMe, nationThem);
        RelationType pre = relation.getType();
        setRelation(nationThemId, nextType);
        assertEquals(pre, relation.getType());
        assertRelationDelayed(nationThem, nextType);
    }

    protected void assertRelationDelayed(Nation nation, RelationType nextType) {
        assertRelationDelayed(nationMe, nation, nextType);
    }

    protected void assertRelationDelayed(Nation nationFrom, Nation nationTo, RelationType nextType) {
        Relation relation;
        relation = relationDao.findRelation(nationFrom, nationTo);
        assertEquals(nextType, relation.getNextType());
        assertNotNull(relation.getSwitchTime());
        assertTrue(eventQueue.cancel(relation));
    }

    protected void thirdChangedTo(RelationType nextType) {
        setRelation(nationThreeId, nextType);
        Relation relation = relationDao.findRelation(nationMe, nationThird);
        assertEquals(nextType, relation.getType());
        assertNull(relation.getNextType());
        assertNull(relation.getSwitchTime());
        assertFalse(eventQueue.cancel(relation));
    }

    protected void thirdChangedToDelayed(RelationType nextType) {
        Relation relation = relationDao.findRelation(nationMe, nationThird);
        RelationType pre = relation.getType();
        setRelation(nationThreeId, nextType);
        relation = relationDao.findRelation(nationMe, nationThird);
        assertEquals(pre, relation.getType());
        assertEquals(nextType, relation.getNextType());
        assertNotNull(relation.getSwitchTime());
        assertTrue(eventQueue.cancel(relation));
    }

}
