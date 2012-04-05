package com.kenstevens.stratinit.server.remote.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.event.EventQueue;
import com.kenstevens.stratinit.server.remote.ThreePlayerBase;
import com.kenstevens.stratinit.type.RelationType;

@Ignore
public abstract class ThreeRelationManagerTest extends ThreePlayerBase {
	@Autowired
	private EventQueue eventQueue;

	
	// TODO REF repeated with TwoPlayerBase
	protected void changedTo(RelationType nextType) {
		Result<SIRelation> result = stratInit.setRelation(nationThemId, nextType);
		assertResult(result);
		assertRelationChanged(nationThem, nextType);
	}

	protected void assertRelationChanged(Nation nation, RelationType nextType) {
		assertRelationChanged(nationMe, nation, nextType);
	}

	protected void assertRelationChanged(Nation nationFrom, Nation nationTo, RelationType nextType) {
		Relation relation = gameDao.findRelation(nationFrom, nationTo);
		assertEquals(nextType, relation.getType());
		assertNull(relation.getNextType());
		assertNull(relation.getSwitchTime());
		assertFalse(eventQueue.cancel(relation));
	}

	// TODO REF repeated with TwoPlayerBase
	protected void changedToDelayed(RelationType nextType) {
		Relation relation = gameDao.findRelation(nationMe, nationThem);
		RelationType pre = relation.getType();
		Result<SIRelation> result = stratInit.setRelation(nationThemId, nextType);
		assertResult(result);
		assertEquals(result.toString(), pre, relation.getType());
		assertRelationDelayed(nationThem, nextType);
	}

	protected void assertRelationDelayed(Nation nation, RelationType nextType) {
		assertRelationDelayed(nationMe, nation, nextType);
	}
	protected void assertRelationDelayed(Nation nationFrom, Nation nationTo, RelationType nextType) {
		Relation relation;
		relation = gameDao.findRelation(nationFrom, nationTo);
		assertEquals(nextType, relation.getNextType());
		assertNotNull(relation.getSwitchTime());
		assertTrue(eventQueue.cancel(relation));
	}
	
	protected void thirdChangedTo(RelationType nextType) {
		Result<SIRelation> result = stratInit.setRelation(nationThreeId, nextType);
		assertResult(result);
		Relation relation = gameDao.findRelation(nationMe, nationThird);
		assertEquals(result.toString(), nextType, relation.getType());
		assertNull(relation.getNextType());
		assertNull(relation.getSwitchTime());
		assertFalse(eventQueue.cancel(relation));
	}

	protected void thirdChangedToDelayed(RelationType nextType) {
		Relation relation = gameDao.findRelation(nationMe, nationThird);
		RelationType pre = relation.getType();
		Result<SIRelation> result = stratInit.setRelation(nationThreeId, nextType);
		assertResult(result);
		relation = gameDao.findRelation(nationMe, nationThird);
		assertEquals(result.toString(), pre, relation.getType());
		assertEquals(result.toString(), nextType, relation.getNextType());
		assertNotNull(relation.getSwitchTime());
		assertTrue(eventQueue.cancel(relation));
	}
}
