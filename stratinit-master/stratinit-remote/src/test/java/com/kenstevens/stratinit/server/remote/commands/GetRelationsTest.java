package com.kenstevens.stratinit.server.remote.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.RelationType;

public class GetRelationsTest extends TwoPlayerBase {
	private List<SIRelation> relations;

	@Test
	public void nation2() {
		relations = stratInit.getRelations().getValue();
		SIRelation sirelation = getRelation(nationThemId);
		assertEquals(RelationType.NEUTRAL, sirelation.meToThem);
		assertEquals(RelationType.NEUTRAL, sirelation.themToMe);
		assertNull(sirelation.myNextType);
		assertNull(sirelation.mineSwitches);
	}

	@Test
	public void nation2war() {
		declareWar();
		relations = stratInit.getRelations().getValue();
		SIRelation sirelation = getRelation(nationThemId);
		assertEquals(RelationType.WAR, sirelation.themToMe);
		assertEquals(RelationType.WAR, sirelation.meToThem);
		assertNull(sirelation.myNextType);
		assertNull(sirelation.mineSwitches);
	}

	@Test
	public void nation2warBack() {
		warDeclared();
		relations = stratInit.getRelations().getValue();
		SIRelation sirelation = getRelation(nationThemId);
		assertEquals(RelationType.NEUTRAL, sirelation.meToThem);
		assertEquals(RelationType.WAR, sirelation.themToMe);
		assertNull(sirelation.myNextType);
		assertNull(sirelation.mineSwitches);
	}

	@Test
	public void nation1() {
		relations = stratInit.getRelations().getValue();
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
