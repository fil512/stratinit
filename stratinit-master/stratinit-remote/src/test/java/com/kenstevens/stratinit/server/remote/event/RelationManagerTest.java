package com.kenstevens.stratinit.server.remote.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.RelationType;

@Ignore
public class RelationManagerTest extends TwoPlayerBase {
	@Autowired
	private EventQueue eventQueue;
	
	protected void changedTo(RelationType nextType) {
		Result<SIRelation> result = stratInit.setRelation(nationThemId, nextType);
		assertResult(result);
		Relation relation = gameDao.findRelation(nationMe, nationThem);
		assertEquals(result.toString(), nextType, relation.getType());
		assertNull(relation.getNextType());
		assertNull(relation.getSwitchTime());
		assertFalse(eventQueue.cancel(relation));
	}

	protected void changedToDelayed(RelationType nextType) {
		Relation relation = gameDao.findRelation(nationMe, nationThem);
		RelationType pre = relation.getType();
		Result<SIRelation> result = stratInit.setRelation(nationThemId, nextType);
		assertResult(result);
		relation = gameDao.findRelation(nationMe, nationThem);
		assertEquals(result.toString(), pre, relation.getType());
		assertEquals(result.toString(), nextType, relation.getNextType());
		assertNotNull(relation.getSwitchTime());
		assertTrue(eventQueue.cancel(relation));
	}
}
