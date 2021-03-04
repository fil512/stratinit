package com.kenstevens.stratinit.server.rest.svc;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Relation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class RelationSvc {
    @Autowired
    private GameDao gameDao;

    public List<SIRelation> getRelations(Nation nation) {
        Collection<Relation> relations = gameDao.getMyRelations(nation);
        Map<Nation, Relation> map = gameDao.getTheirRelationsAsMap(nation);
        List<SIRelation> retval = new ArrayList<SIRelation>();
        for (Relation relation : relations) {
            // TODO REF
            Relation themToMe = map.get(relation.getTo());
            retval.add(new SIRelation(relation, themToMe));
        }
        return retval;
    }
}
