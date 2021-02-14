package com.kenstevens.stratinit.server.remote.event;

import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.RelationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class TwoPlayerRelationChangeTest extends RelationManagerTest {

	@BeforeEach
	public void startGame() {
		Date now = new Date();
		Date then = new Date(now.getTime() - 1000);
		testGame.setStartTime(then);
		testGame.setMapped(then);
	}

	@Test
	public void neutralToWar() {
		changedTo(RelationType.WAR);
	}

	@Test
	public void neutralToAllied() {
		changedTo(RelationType.ALLIED);
	}

	@Test
	public void neutralToFriendly() {
		changedTo(RelationType.FRIENDLY);
	}

	@Test
	public void neutralToMe() {
		Result<SIRelation> result = stratInit.setRelation(nationThemId, RelationType.ME);
		assertFalseResult(result);
	}

	@Test
	public void allyToFriendly() {
		neutralToAllied();
		changedTo(RelationType.FRIENDLY);
	}
	@Test
	public void allyToNeutral() {
		neutralToAllied();
		changedTo(RelationType.NEUTRAL);
	}
	@Test
	public void allyToNeutralMutual() {
		neutralToAllied();
		allianceDeclared();
		changedToDelayed(RelationType.NEUTRAL);
	}
	@Test
	public void allyToWar() {
		neutralToAllied();
		changedToDelayed(RelationType.WAR);
	}
	@Test
	public void friendlyToAllied() {
		neutralToFriendly();
		changedTo(RelationType.ALLIED);
	}
	@Test
	public void friendlyToNeutral() {
		neutralToFriendly();
		changedTo(RelationType.NEUTRAL);
	}
	@Test
	public void friendlyToNeutralMutual() {
		neutralToFriendly();
		friendlyDeclared();
		changedToDelayed(RelationType.NEUTRAL);
	}
	@Test
	public void friendlyToWar() {
		neutralToFriendly();
		changedToDelayed(RelationType.WAR);
	}
}
