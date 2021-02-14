package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.RelationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameDaoTest extends TwoPlayerBase {
	@Autowired
	private GameDao gameDao;

	@Test
	public void getMyRelations() {
		Map<Nation, RelationType> relationMap = gameDao.getMyRelationsAsMap(nationMe);
		assertEquals(RelationType.ME, relationMap.get(nationMe));
		assertEquals(RelationType.NEUTRAL, relationMap.get(nationThem));
	}

	@Test
	public void getTheirRelations() {
		Map<Nation, RelationType> relationMap = gameDao.getTheirRelationTypesAsMap(nationMe);
		assertEquals(RelationType.ME, relationMap.get(nationMe));
		assertEquals(RelationType.NEUTRAL, relationMap.get(nationThem));
	}

	@Test
	public void getMyRelationsReverse() {
		Map<Nation, RelationType> relationMap = gameDao.getMyRelationsAsMap(nationThem);
		assertEquals(RelationType.ME, relationMap.get(nationThem));
		assertEquals(RelationType.NEUTRAL, relationMap.get(nationMe));
	}

	@Test
	public void getTheirRelationsReverse() {
		Map<Nation, RelationType> relationMap = gameDao.getTheirRelationTypesAsMap(nationThem);
		assertEquals(RelationType.ME, relationMap.get(nationThem));
		assertEquals(RelationType.NEUTRAL, relationMap.get(nationMe));
	}

}
