package com.kenstevens.stratinit.client.server.rest.svc;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Relation;
import com.kenstevens.stratinit.dao.RelationDao;
import com.kenstevens.stratinit.dto.SIRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class RelationSvc {
    @Autowired
    private RelationDao relationDao;

    public List<SIRelation> getRelations(Nation nation) {
        Collection<Relation> relations = relationDao.getMyRelations(nation);
        Map<Nation, Relation> map = relationDao.getTheirRelationsAsMap(nation);
        List<SIRelation> retval = new ArrayList<SIRelation>();
        for (Relation relation : relations) {
            // TODO REF
            Relation themToMe = map.get(relation.getTo());
            retval.add(new SIRelation(relation, themToMe));
        }
        return retval;
    }
}
