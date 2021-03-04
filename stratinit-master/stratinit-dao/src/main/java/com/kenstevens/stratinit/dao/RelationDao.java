package com.kenstevens.stratinit.dao;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.model.RelationPK;
import com.kenstevens.stratinit.model.audit.RelationChangeAudit;
import com.kenstevens.stratinit.repo.RelationChangeAuditRepo;
import com.kenstevens.stratinit.repo.RelationRepo;
import com.kenstevens.stratinit.type.RelationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RelationDao extends CacheDao {
    @Autowired
    private RelationRepo relationRepo;
    @Autowired
    private RelationChangeAuditRepo relationChangeAuditRepo;

    public void save(Relation relation) {
        relationRepo.save(relation);
        getGameCache(relation.getGame()).add(relation);
    }

    public void save(RelationChangeAudit relationChangeAudit) {
        relationChangeAuditRepo.save(relationChangeAudit);
    }

    public void remove(Relation relation) {
        relationRepo.delete(relation);
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
        return Collections2.filter(relations, new Predicate<Relation>() {
            public boolean apply(Relation input) {
                return input.getGame().equals(game)
                        && input.getSwitchTime() != null;
            }
        });
    }

    private Collection<Nation> getMutualRelations(Nation nation, final Set<RelationType> relations) {
        Collection<Relation> myAllies = Collections2.filter(getMyRelations(nation), new Predicate<Relation>() {
            public boolean apply(Relation input) {
                return relations.contains(input.getType());
            }
        });
        final Map<Nation, RelationType> theirRelationTypes = getTheirRelationTypesAsMap(nation);
        Collection<Relation> mutualAllies = Collections2.filter(myAllies, input -> relations.contains(theirRelationTypes.get(input.getTo())));
        return Collections2.transform(mutualAllies, Relation::getTo);
    }

    // TODO REF pull predicates and functions out into classes
    public Collection<Nation> getAllies(Nation nation) {
        Set<RelationType> relations = Sets.immutableEnumSet(RelationType.ALLIED);
        return getMutualRelations(nation, relations);
    }

    public Collection<Relation> getMyRelations(final Nation nation) {
        List<Relation> relations = getGameCache(nation.getGameId()).getRelations();
        return Collections2.filter(relations, input -> input.getFrom().equals(nation));
    }

    public Collection<Nation> getMyRelations(final Nation nation, final RelationType relationType) {
        List<Relation> relations = getGameCache(nation.getGameId()).getRelations();
        Collection<Relation> myAllies = Collections2.filter(relations, relation -> relation.getFrom().equals(nation) && relation.getType().equals(relationType));
        return Collections2.transform(myAllies, Relation::getTo);
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
        return Collections2.filter(relations, input -> input.getTo().equals(nation));
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
