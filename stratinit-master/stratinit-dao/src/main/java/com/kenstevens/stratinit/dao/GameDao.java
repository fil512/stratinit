package com.kenstevens.stratinit.dao;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.cache.GameLoader;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.model.audit.RelationChangeAudit;
import com.kenstevens.stratinit.repo.*;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.util.GameNameFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class GameDao extends CacheDao {
    @Autowired
    private GameRepo gameRepo;
    @Autowired
    private NationRepo nationRepo;
    @Autowired
    private RelationRepo relationRepo;
    @Autowired
    private RelationChangeAuditRepo relationChangeAuditRepo;
    @Autowired
    private GameBuildAuditRepo gameBuildAuditRepo;
    @Autowired
    private CityRepo cityRepo;
    @Autowired
    private GameLoader gameLoader;
    @Autowired
    private SectorRepo sectorRepo;
    @Autowired
    private UnitRepo unitRepo;

    public Game findGame(int gameId) {
        GameCache gameCache = getGameCache(gameId);
        if (gameCache == null) {
            return null;
        }
        return gameCache.getGame();
    }

    public Nation findNation(Game game, Player player) {
        return findNation(game.getId(), player);
    }

    public Nation findNation(int gameId, Player player) {
        GameCache gameCache = getGameCache(gameId);
        if (gameCache == null) {
            return null;
        }
        return gameCache.getNation(player);
    }

    public Relation findRelation(Nation from, Nation to) {
        return findRelation(new RelationPK(from, to));
    }

    public Relation findRelation(RelationPK relationPK) {
        return getGameCache(relationPK.getFrom().getGameId()).findRelation(
                relationPK);
    }

    public void flush() {
        for (GameCache gameCache : dataCache.getGameCaches()) {
            gameLoader.flush(gameCache);
        }
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

    public List<Game> getAllGames() {
        return dataCache.getAllGames();
    }

    public List<Nation> getAllNations(int gameId) {
        return getGameCache(gameId).getNations();
    }

    public List<Nation> getAllNations() {
        return dataCache.getAllNations();
    }

    private Collection<Nation> getMutualRelations(Nation nation, final Set<RelationType> relations) {
        Collection<Relation> myAllies = Collections2.filter(getMyRelations(nation), new Predicate<Relation>() {
            public boolean apply(Relation input) {
                return relations.contains(input.getType());
            }
        });
        final Map<Nation, RelationType> theirRelationTypes = getTheirRelationTypesAsMap(nation);
        Collection<Relation> mutualAllies = Collections2.filter(myAllies, new Predicate<Relation>() {
            public boolean apply(Relation input) {
                return relations.contains(theirRelationTypes.get(input.getTo()));
            }
        });
        return Collections2.transform(mutualAllies, new Function<Relation, Nation>() {
            @Override
            public Nation apply(Relation relation) {
                return relation.getTo();
            }
        });
    }

    // TODO REF pull predicates and functions out into classes
    public Collection<Nation> getAllies(Nation nation) {
        Set<RelationType> relations = Sets.immutableEnumSet(RelationType.ALLIED);
        return getMutualRelations(nation, relations);
    }

    public Collection<Relation> getMyRelations(final Nation nation) {
        List<Relation> relations = getGameCache(nation.getGameId()).getRelations();
        return Collections2.filter(relations, new Predicate<Relation>() {
            public boolean apply(Relation input) {
                return input.getFrom().equals(nation);
            }
        });
    }

    public Collection<Nation> getMyRelations(final Nation nation, final RelationType relationType) {
        List<Relation> relations = getGameCache(nation.getGameId()).getRelations();
        Collection<Relation> myAllies = Collections2.filter(relations, new Predicate<Relation>() {
            public boolean apply(Relation relation) {
                return relation.getFrom().equals(nation) && relation.getType().equals(relationType);
            }
        });
        return Collections2.transform(myAllies, new Function<Relation, Nation>() {
            @Override
            public Nation apply(Relation relation) {
                return relation.getTo();
            }
        });
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
        return Collections2.filter(relations, new Predicate<Relation>() {
            public boolean apply(Relation input) {
                return input.getTo().equals(nation);
            }
        });
    }

    public Map<Nation, RelationType> getTheirRelationTypesAsMap(Nation nation) {
        Collection<Relation> relations = getTheirRelations(nation);
        Map<Nation, RelationType> retval = new HashMap<Nation, RelationType>();
        for (Relation relation : relations) {
            retval.put(relation.getFrom(), relation.getType());
        }
        return retval;
    }

    public Nation getNation(int gameId, int nationId) {
        Collection<Nation> nations = getGameCache(gameId).getNations();
        for (Nation nation : nations) {
            if (nation.getNationId() == nationId) {
                return nation;
            }
        }
        return null;
    }

    public List<Nation> getNations(Game game) {
        return getGameCache(game).getNations();
    }

    public List<Nation> getNations(Player player) {
        List<GameCache> gameCaches = dataCache.getGameCaches();
        List<Nation> retval = new ArrayList<Nation>();
        for (GameCache gameCache : gameCaches) {
            Nation nation = gameCache.getNation(player);
            if (nation != null) {
                retval.add(nation);
            }
        }
        return retval;
    }

    public List<Relation> getRelations(Game game) {
        return getGameCache(game).getRelations();
    }

    public Relation getReverse(Relation relation) {
        return findRelation(relation.getTo(), relation.getFrom());
    }

    public void merge(Game game) {
        if (game.isEnabled()) {
            getGameCache(game).setModified(true);
        } else {
            gameRepo.save(game);
            dataCache.remove(game);
        }
    }

    public void markCacheModified(Nation nation) {
        getNationCache(nation).setModified(true);
    }

    public void markCacheModified(Relation relation) {
        getGameCache(relation.getGame()).setModified(true);
    }

    public void save(Nation nation) {
        nationRepo.save(nation);
        getGameCache(nation.getGameId()).add(nation);
    }

    @Transactional
    public void save(Game game) {
        game.setCreated();
        if (game.getGamename() == null) {
            game.setGamename("");
        }
        gameRepo.save(game);
        getGameCache(game.getId());
        if (game.getGamename().isEmpty()) {
            game.setGamename(GameNameFile.getName(game.getId()));
        }
    }

    public void save(Relation relation) {
        relationRepo.save(relation);
        getGameCache(relation.getGame()).add(relation);
    }

    public void save(RelationChangeAudit relationChangeAudit) {
        relationChangeAuditRepo.save(relationChangeAudit);
    }

    @Transactional
    public void remove(Game game) {
        nationRepo.deleteAll(nationRepo.findAll(QNation.nation.nationPK.game.eq(game)));
        unitRepo.deleteAll(unitRepo.findAll(QUnit.unit.nation.nationPK.game.eq(game)));
        sectorRepo.deleteAll(sectorRepo.findAll(QSector.sector.sectorPK.game.eq(game)));
        gameRepo.delete(game);
        dataCache.remove(game);
    }

    @Transactional
    public void removeGame(int id) {
        remove(getGameCache(id).getGame());
    }

    public List<Game> getUnjoinedGames(Player player) {
        List<Game> retval = new ArrayList<Game>();
        for (GameCache gameCache : dataCache.getGameCaches()) {
            if (gameCache.getNation(player) == null) {
                retval.add(gameCache.getGame());
            }
        }
        return retval;
    }

    public List<GameBuildAudit> getGameBuildAudit() {
        return gameBuildAuditRepo.findAll();
    }

    public void remove(Relation relation) {
        relationRepo.delete(relation);
    }

    public void remove(City city) {
        cityRepo.delete(city);
    }

    public Collection<Nation> getFriendsAndAllies(Nation nation) {
        Set<RelationType> relations = Sets.immutableEnumSet(RelationType.ALLIED, RelationType.FRIENDLY);
        return getMutualRelations(nation, relations);
    }

    public List<Game> getAllStartedGames() {
        List<Game> allGames = dataCache.getAllGames();
        return Lists.newArrayList(Collections2.filter(allGames, new Predicate<Game>() {
            @Override
            public boolean apply(Game game) {
                return game.hasStarted();
            }
        }));
    }
}
