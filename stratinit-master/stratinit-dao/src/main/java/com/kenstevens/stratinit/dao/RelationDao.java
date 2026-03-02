package com.kenstevens.stratinit.dao;

import com.google.common.collect.Sets;
import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Relation;
import com.kenstevens.stratinit.client.model.RelationPK;
import com.kenstevens.stratinit.client.model.audit.RelationChangeAudit;
import com.kenstevens.stratinit.repo.RelationChangeAuditRepo;
import com.kenstevens.stratinit.repo.RelationRepo;
import com.kenstevens.stratinit.type.RelationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RelationDao extends CacheDao {
    @Autowired
    private RelationRepo relationRepo;
    @Autowired
    private RelationChangeAuditRepo relationChangeAuditRepo;

    public void save(Relation relation) {
        if (!skipDb()) {
            relationRepo.save(relation);
        }
        getGameCache(relation.getGame()).add(relation);
    }

    public void save(RelationChangeAudit relationChangeAudit) {
        if (skipDb()) {
            return;
        }
        relationChangeAuditRepo.save(relationChangeAudit);
    }

    public void remove(Relation relation) {
        if (!skipDb()) {
            relationRepo.delete(relation);
        }
    }


    public Relation findRelation(Nation from, Nation to) {
        return findRelation(new RelationPK(from, to));
    }

    public Relation findRelation(RelationPK relationPK) {
        return getGameCache(relationPK.getFrom().getGameId()).findRelation(
                relationPK);
    }

    public Collection<Relation> getAllChangingRelations(final Game game) {
        List<Relation> relations = getGameCache(game).getRelations();
        return relations.stream()
                .filter(r -> r.getGame().equals(game) && r.getSwitchTime() != null)
                .collect(Collectors.toList());
    }

    private Collection<Nation> getMutualRelations(Nation nation, final Set<RelationType> relationTypes) {
        final Map<Nation, RelationType> theirRelationTypes = getTheirRelationTypesAsMap(nation);
        return getMyRelations(nation).stream()
                .filter(r -> relationTypes.contains(r.getType()))
                .filter(r -> relationTypes.contains(theirRelationTypes.get(r.getTo())))
                .map(Relation::getTo)
                .collect(Collectors.toList());
    }

    // TODO REF pull predicates and functions out into classes
    public Collection<Nation> getAllies(Nation nation) {
        Set<RelationType> relations = Sets.immutableEnumSet(RelationType.ALLIED);
        return getMutualRelations(nation, relations);
    }

    public Collection<Relation> getMyRelations(final Nation nation) {
        List<Relation> relations = getGameCache(nation.getGameId()).getRelations();
        return relations.stream()
                .filter(input -> input.getFrom().equals(nation))
                .collect(Collectors.toList());
    }

    public Collection<Nation> getMyRelations(final Nation nation, final RelationType relationType) {
        List<Relation> relations = getGameCache(nation.getGameId()).getRelations();
        return relations.stream()
                .filter(r -> r.getFrom().equals(nation) && r.getType().equals(relationType))
                .map(Relation::getTo)
                .collect(Collectors.toList());
    }

    public Map<Nation, RelationType> getMyRelationsAsMap(Nation nation) {
        Collection<Relation> relations = getMyRelations(nation);
        Map<Nation, RelationType> retval = new HashMap<Nation, RelationType>();
        for (Relation relation : relations) {
            retval.put(relation.getTo(), relation.getType());
        }
        return retval;
    }


    public Map<Nation, Relation> getTheirRelationsAsMap(Nation nation) {
        Collection<Relation> relations = getTheirRelations(nation);
        Map<Nation, Relation> retval = new HashMap<Nation, Relation>();
        for (Relation relation : relations) {
            retval.put(relation.getFrom(), relation);
        }
        return retval;
    }

    public Collection<Relation> getTheirRelations(final Nation nation) {
        List<Relation> relations = getGameCache(nation.getGameId()).getRelations();
        return relations.stream()
                .filter(input -> input.getTo().equals(nation))
                .collect(Collectors.toList());
    }

    public Map<Nation, RelationType> getTheirRelationTypesAsMap(Nation nation) {
        Collection<Relation> relations = getTheirRelations(nation);
        Map<Nation, RelationType> retval = new HashMap<Nation, RelationType>();
        for (Relation relation : relations) {
            retval.put(relation.getFrom(), relation.getType());
        }
        return retval;
    }

    public List<Relation> getRelations(Game game) {
        return getGameCache(game).getRelations();
    }

    public Relation getReverse(Relation relation) {
        return findRelation(relation.getTo(), relation.getFrom());
    }


    public void markCacheModified(Relation relation) {
        getGameCache(relation.getGame()).setModified(true);
    }

    public Collection<Nation> getFriendsAndAllies(Nation nation) {
        Set<RelationType> relations = Sets.immutableEnumSet(RelationType.ALLIED, RelationType.FRIENDLY);
        return getMutualRelations(nation, relations);
    }
}
