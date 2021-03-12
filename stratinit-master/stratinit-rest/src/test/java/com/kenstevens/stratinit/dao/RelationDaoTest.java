package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.RelationType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RelationDaoTest extends TwoPlayerBase {

    @Test
    public void getMyRelations() {
        Map<Nation, RelationType> relationMap = relationDao.getMyRelationsAsMap(nationMe);
        assertEquals(RelationType.ME, relationMap.get(nationMe));
        assertEquals(RelationType.NEUTRAL, relationMap.get(nationThem));
    }

    @Test
    public void getTheirRelations() {
        Map<Nation, RelationType> relationMap = relationDao.getTheirRelationTypesAsMap(nationMe);
        assertEquals(RelationType.ME, relationMap.get(nationMe));
        assertEquals(RelationType.NEUTRAL, relationMap.get(nationThem));
    }

    @Test
    public void getMyRelationsReverse() {
        Map<Nation, RelationType> relationMap = relationDao.getMyRelationsAsMap(nationThem);
        assertEquals(RelationType.ME, relationMap.get(nationThem));
        assertEquals(RelationType.NEUTRAL, relationMap.get(nationMe));
    }

    @Test
    public void getTheirRelationsReverse() {
        Map<Nation, RelationType> relationMap = relationDao.getTheirRelationTypesAsMap(nationThem);
        assertEquals(RelationType.ME, relationMap.get(nationThem));
        assertEquals(RelationType.NEUTRAL, relationMap.get(nationMe));
    }

}
