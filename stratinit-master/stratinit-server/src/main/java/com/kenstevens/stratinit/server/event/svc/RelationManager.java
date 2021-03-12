package com.kenstevens.stratinit.server.event.svc;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Relation;
import com.kenstevens.stratinit.client.model.audit.RelationChangeAudit;
import com.kenstevens.stratinit.dao.RelationDao;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.RelationDaoService;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.RelationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;

@Service
public class RelationManager {
    @Autowired
    private EventQueue eventQueue;
    @Autowired
    private RelationDao relationDao;
    @Autowired
    private RelationDaoService relationDaoService;

    // TODO REF
    public Result<Relation> changeRelation(Relation relation,
                                           RelationType nextType, boolean override) {
        Relation reverse = relationDao.findRelation(relation.getTo(), relation
                .getFrom());

        if (override) {
            String message = setNextRelationTypeNow(relation, nextType);
            return new Result<>(message, true, relation);
        }

        if (nextType == relation.getType()) {
            // No change, but cancel out any next type we might have
            setNextRelationTypeNow(relation, nextType);
            return new Result<>("Cancelling relation change.", false,
                    relation);
        } else if (nextType == relation.getNextType()) {
            return new Result<>("Nothing to change.", false, relation);
            // Do nothing. Don't want to stop timer.
        } else {
            return switchRelation(relation, nextType, reverse);
        }

    }

    private Result<Relation> switchRelation(Relation relation,
                                            RelationType nextType, Relation reverse) {
        if (nextType.compareTo(relation.getType()) > 0) {
            return improveRelations(relation, nextType);
        } else if (nextType.compareTo(relation.getType()) < 0) {
            return degradeRelations(relation, nextType, reverse);
        } else {
            throw new IllegalStateException();
        }
    }

    private Result<Relation> improveRelations(Relation relation,
                                              RelationType nextType) {
        if (nextType == RelationType.ALLIED && !roomForAllies(relation)) {
            return new Result<>(roomForAlliesString(relation), false,
                    relation);
        } else {
            // Improving relations
            String message = setNextRelationTypeNow(relation, nextType);
            return new Result<>(message, true, relation);
        }
    }

    private boolean roomForAllies(Relation relation) {
        return roomForAlliesString(relation).isEmpty();
    }

    private String roomForAlliesString(Relation relation) {
        Collection<Nation> myAllies = relationDao.getMyRelations(relation.getFrom(), RelationType.ALLIED);
        Collection<Nation> theirAllies = relationDao.getAllies(relation.getTo());
        if (myAllies.size() >= Constants.MAX_ALLIES) {
            return "You already have an ally";
        } else if (theirAllies.size() > Constants.MAX_ALLIES) {
            return relation.getTo() + " already has an ally.";
        } else {
            return "";
        }
    }

    private Result<Relation> degradeRelations(Relation relation,
                                              RelationType nextType, Relation reverse) {
        // Lowering relations
        if (relation.getType() == RelationType.ALLIED
                || relation.getType() == RelationType.FRIENDLY) {
            if (nextType == RelationType.FRIENDLY
                    || nextType.compareTo(reverse.getType()) >= 0) {
                // Can downgrade from allied to friendly without notice
                // Can downgrade to match without notice
                String message = setNextRelationTypeNow(relation, nextType);
                return new Result<>(message, true, relation);
            } else {
                String message = setNextRelationTypeLater(relation, nextType,
                        Constants.ALLIED_FRIENDLY_CANCEL_TIME_HOURS);
                return new Result<>(message, true, relation);
            }
        } else {
            String message = setNextRelationTypeNow(relation, nextType);
            return new Result<>(message, true, relation);
        }
    }

    private String setNextRelationTypeLater(Relation relation, RelationType nextType, int hours) {
        if (relation.getFrom().getGame().isBlitz()) {
            return setNextRelationTypeNow(relation, nextType);
        }
        relation.setNextType(nextType);
        int millis = hours * 60 * 60 * 1000;
        Date later = new Date(new Date().getTime() + millis);
        relation.setSwitchTime(later);
        eventQueue.schedule(relation);
        RelationChangeAudit relationChangeAudit = new RelationChangeAudit(
                relation);
        relationDao.save(relationChangeAudit);
        relationDao.markCacheModified(relation);
        return "scheduled relation change with " + relation.getTo() + " to "
                + nextType + " " + hours + " hours from now.";
    }

    private String setNextRelationTypeNow(Relation relation,
                                          RelationType nextType) {
        relation.setNextType(nextType);
        // TODO REF not necessary to set switch time to null
        relation.setSwitchTime(null);
        eventQueue.cancel(relation);
        relationDaoService.switchRelation(relation);
        return "changed relation with " + relation.getTo() + " to " + nextType;
    }

}
