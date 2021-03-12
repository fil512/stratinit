package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Relation;
import com.kenstevens.stratinit.client.model.RelationPK;
import com.kenstevens.stratinit.client.model.audit.RelationChangeAudit;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.RelationDao;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.event.svc.EventQueue;
import com.kenstevens.stratinit.server.event.svc.RelationManager;
import com.kenstevens.stratinit.type.RelationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class RelationDaoService {
    @Autowired
    private GameDao gameDao;
    @Autowired
    private RelationDao relationDao;
    @Autowired
    private MessageDaoService messageDaoService;
    @Autowired
    private RelationManager relationManager;
    @Autowired
    private EventQueue eventQueue;

    public void setRelations(Nation me) {
        List<Nation> nations = gameDao.getNations(me.getGame());
        for (Nation nation : nations) {
            Relation relation = new Relation(me, nation);
            if (nation.equals(me)) {
                relation.setType(RelationType.ME);
            }
            relationDao.save(relation);
            if (!nation.equals(me)) {
                relation = new Relation(nation, me);
                relationDao.save(relation);
            }
        }
    }

    public Result<Relation> setRelation(Nation nation, Nation target,
                                        RelationType newRelation, boolean override) {
        if (nation.equals(target)) {
            return new Result<>(
                    "You can't change relations with yourself", false);
        }
        Relation relation = relationDao.findRelation(nation, target);
        if (newRelation == relation.getType()) {
            return new Result<>("Nothing to change.", false, relation);
        }
        Result<Relation> result = relationManager.changeRelation(relation,
                newRelation, override);
        if (result.isSuccess()) {
            messageDaoService.notify(target, nation.toString()
                    + " diplomatic update: " + newRelation, nation.toString()
                    + " " + result);
        }

        return result;
    }

    public void switchRelation(RelationPK relationPK) {
        Relation relation = relationDao.findRelation(relationPK);
        switchRelation(relation);
    }

    public void switchRelation(Relation relationToSwitch) {
        Relation relation = relationDao.findRelation(relationToSwitch
                .getRelationPK());
        Relation reverse = relationDao.getReverse(relation);
        if (relation.getNextType() == null) {
            // Race condition
            return;
        }
        moveRelationForward(relation);
        relationDao.markCacheModified(relation);
        // If the reverse relation is better than me, then degrade it down to
        // me.
        RelationType type = relation.getType();
        if (reverse.getType().compareTo(type) > 0) {
            changeRelation(reverse, type);
        }

        if (type.compareTo(RelationType.NEUTRAL) < 0) {
            degradeAllyRelations(relation);
        } else if (type == RelationType.ALLIED) {
            // thirdThemWarThirdMeFriendIAllyThemShouldSwitchThirdNeutralToMe
            ifXatWarWithThemAndXFriendlyWithMeDropXToNeutralWithMe(relation);
        }
    }

    private void ifXatWarWithThemAndXFriendlyWithMeDropXToNeutralWithMe(
            Relation relation) {
        Nation me = relation.getFrom();
        Nation them = relation.getTo();
        for (Nation x : gameDao.getNations(relation.getGame())) {
            Relation xToThem = relationDao.findRelation(x, them);
            if (xToThem.getType() != RelationType.WAR) {
                continue;
            }
            // x is at war with them
            Relation xToMe = relationDao.findRelation(x, me);
            if (xToMe.getType().compareTo(RelationType.NEUTRAL) > 0) {
                changeRelation(xToMe, RelationType.NEUTRAL);
            }
        }
    }

    private void changeRelation(Relation relation, RelationType type) {
        relation.setNextType(type);
        moveRelationForward(relation);
        relationDao.markCacheModified(relation);
        eventQueue.cancel(relation);
    }

    private void degradeAllyRelations(Relation relation) {
        Nation me = relation.getFrom();
        Nation targetNation = relation.getTo();
        for (Nation ally : relationDao.getAllies(targetNation)) {
            Relation allyRelation = relationDao.findRelation(me, ally);
            if (allyRelation.getType().compareTo(RelationType.NEUTRAL) > 0) {
                changeRelation(allyRelation, RelationType.NEUTRAL);
            }
            Relation allyReverse = relationDao.findRelation(ally, me);
            if (allyReverse.getType().compareTo(RelationType.NEUTRAL) > 0) {
                changeRelation(allyReverse, RelationType.NEUTRAL);
            }
        }
    }

    private void moveRelationForward(Relation relation) {
        RelationChangeAudit relationChangeAudit = new RelationChangeAudit(
                relation);
        relationDao.save(relationChangeAudit);
        relation.setType(relation.getNextType());
        relation.setNextType(null);
        relation.setSwitchTime(null);
        if (relation.getType() == RelationType.ALLIED) {
            Relation reverse = relationDao.getReverse(relation);
            if (reverse.getType() == RelationType.ALLIED) {
                messageDaoService.postBulletin(relation.getGame(), relation
                        .getFrom()
                        + " and "
                        + relation.getTo()
                        + " have formed an alliance", null);
            }
        }
    }

    public void remove(Relation relation) {
        eventQueue.cancel(relation);
        relationDao.remove(relation);
    }

    public Collection<Nation> getAllies(Nation nation) {
        return relationDao.getAllies(nation);
    }

    public Map<Nation, RelationType> getMyRelationsAsMap(Nation nation) {
        return relationDao.getMyRelationsAsMap(nation);
    }

    public Map<Nation, RelationType> getTheirRelationTypesAsMap(Nation nation) {
        return relationDao.getTheirRelationTypesAsMap(nation);
    }

    public Map<Nation, Relation> getTheirRelationsAsMap(Nation nation) {
        return relationDao.getTheirRelationsAsMap(nation);
    }
}
