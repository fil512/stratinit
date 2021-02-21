package com.kenstevens.stratinit.server.remote.event;

import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.remote.SIResponseEntity;
import com.kenstevens.stratinit.server.event.svc.EventQueue;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.RelationType;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public class RelationManagerTest extends TwoPlayerBase {
	@Autowired
	private EventQueue eventQueue;

	protected void changedTo(RelationType nextType) {
		SIResponseEntity<SIRelation> result = stratInit.setRelation(nationThemId, nextType);
		assertResult(result);
		Relation relation = gameDao.findRelation(nationMe, nationThem);
		assertEquals(nextType, relation.getType(), result.toString());
		assertNull(relation.getNextType());
		assertNull(relation.getSwitchTime());
		assertFalse(eventQueue.cancel(relation));
	}

	protected void changedToDelayed(RelationType nextType) {
		Relation relation = gameDao.findRelation(nationMe, nationThem);
		RelationType pre = relation.getType();
		SIResponseEntity<SIRelation> result = stratInit.setRelation(nationThemId, nextType);
		assertResult(result);
		relation = gameDao.findRelation(nationMe, nationThem);
		assertEquals(pre, relation.getType(), result.toString());
		assertEquals(nextType, relation.getNextType(), result.toString());
		assertNotNull(relation.getSwitchTime());
		assertTrue(eventQueue.cancel(relation));
	}
}
