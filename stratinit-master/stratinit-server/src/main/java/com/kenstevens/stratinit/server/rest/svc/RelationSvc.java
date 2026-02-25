package com.kenstevens.stratinit.server.rest.svc;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Relation;
import com.kenstevens.stratinit.dao.NationDao;
import com.kenstevens.stratinit.dao.RelationDao;
import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.service.EventLogService;
import com.kenstevens.stratinit.server.service.RelationService;
import com.kenstevens.stratinit.type.GameEventType;
import com.kenstevens.stratinit.type.RelationType;
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
    @Autowired
    private NationDao nationDao;
    @Autowired
    private RelationService relationService;
    @Autowired
    private EventLogService eventLogService;

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

    public Result<SIRelation> setRelation(Nation nation, int nationId, RelationType relationType) {
        eventLogService.logUserEvent(nation, GameEventType.SET_RELATION,
                "Set relation to nation " + nationId + " as " + relationType);
        Nation target = nationDao.getNation(nation.getGameId(), nationId);
        if (nation.equals(target)) {
            return new Result<>("You may not change relations with yourself", false);
        }
        if (relationType == RelationType.ME) {
            return new Result<>("You may not change relations to the ME status", false);
        }
        if (relationType == RelationType.ALLIED) {
            if (!nation.getGame().hasStarted()) {
                return new Result<>("Alliances are not allowed in this game yet.  Alliances will be permitted once the game has started.", false);
            } else if (nation.getGame().isNoAlliances()) {
                return new Result<>("Alliances are not allowed in this game.", false);
            }
        }
        Result<Relation> result = relationService.setRelation(nation, target, relationType, false);
        Map<Nation, Relation> map = relationService.getTheirRelationsAsMap(nation);
        Relation themToMe = map.get(target);
        return new Result<>(result.getMessages(), result.isSuccess(),
                new SIRelation(result.getValue(), themToMe));
    }
}
