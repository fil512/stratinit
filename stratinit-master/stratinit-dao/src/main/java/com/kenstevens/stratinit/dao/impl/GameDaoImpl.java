package com.kenstevens.stratinit.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.cache.GameLoader;
import com.kenstevens.stratinit.dal.GameDal;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.GameBuildAudit;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.model.RelationPK;
import com.kenstevens.stratinit.model.audit.RelationChangeAudit;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.util.GameNameFile;

@Service
public class GameDaoImpl extends CacheDaoImpl implements GameDao {
	@Autowired
	private GameDal gameDal;
	@Autowired
	private GameLoader gameLoader;

	@Override
	public Game findGame(int gameId) {
		GameCache gameCache = getGameCache(gameId);
		if (gameCache == null) {
			return null;
		}
		return gameCache.getGame();
	}

	@Override
	public Nation findNation(Game game, Player player) {
		return findNation(game.getId(), player);
	}

	@Override
	public Nation findNation(int gameId, Player player) {
		GameCache gameCache = getGameCache(gameId);
		if (gameCache == null) {
			return null;
		}
		return gameCache.getNation(player);
	}

	@Override
	public Relation findRelation(Nation from, Nation to) {
		return findRelation(new RelationPK(from, to));
	}

	@Override
	public Relation findRelation(RelationPK relationPK) {
		return getGameCache(relationPK.getFrom().getGameId()).findRelation(
				relationPK);
	}

	@Override
	public void flush() {
		for (GameCache gameCache : dataCache.getGameCaches()) {
			gameLoader.flush(gameCache);
		}
	}

	@Override
	public Collection<Relation> getAllChangingRelations(final Game game) {
		List<Relation> relations = getGameCache(game).getRelations();
		return Collections2.filter(relations, new Predicate<Relation>() {
			public boolean apply(Relation input) {
				return input.getGame().equals(game)
						&& input.getSwitchTime() != null;
			}
		});
	}

	@Override
	public List<Game> getAllGames() {
		return dataCache.getAllGames();
	}

	@Override
	public List<Nation> getAllNations(int gameId) {
		return getGameCache(gameId).getNations();
	}

	@Override
	public List<Nation> getAllNations() {
		return dataCache.getAllNations();
	}

	// FIXME find out where this is being used and either rip out the mutual part of it, or split it into two functions (or maybe already there are 2 functions)
	private Collection<Nation> getRelations(Nation nation, final Set<RelationType> relations) {
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
	@Override
	public Collection<Nation> getAllies(Nation nation) {
		Set<RelationType> relations = Sets.immutableEnumSet(RelationType.ALLIED);
		return getRelations(nation, relations);
	}

	@Override
	public Collection<Relation> getMyRelations(final Nation nation) {
		List<Relation> relations = getGameCache(nation.getGameId()).getRelations();
		return Collections2.filter(relations, new Predicate<Relation>() {
			public boolean apply(Relation input) {
				return input.getFrom().equals(nation);
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


	@Override
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

	@Override
	public Nation getNation(int gameId, int nationId) {
		Collection<Nation> nations = getGameCache(gameId).getNations();
		for (Nation nation : nations) {
			if (nation.getNationId() == nationId) {
				return nation;
			}
		}
		return null;
	}

	@Override
	public List<Nation> getNations(Game game) {
		return getGameCache(game).getNations();
	}

	@Override
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

	@Override
	public List<Relation> getRelations(Game game) {
		return getGameCache(game).getRelations();
	}

	@Override
	public Relation getReverse(Relation relation) {
		return findRelation(relation.getTo(), relation.getFrom());
	}

	@Override
	public void merge(Game game) {
		if (game.isEnabled()) {
			getGameCache(game).setModified(true);
		} else {
			gameDal.flush(game);
			dataCache.remove(game);
		}
	}

	@Override
	public void merge(Nation nation) {
		getNationCache(nation).setModified(true);
	}

	@Override
	public void merge(Relation relation) {
		getGameCache(relation.getGame()).setModified(true);
	}

	@Override
	public void persist(Nation nation) {
		gameDal.persist(nation);
		getGameCache(nation.getGameId()).add(nation);
	}

	@Override
	public void persist(Game game) {
		game.setCreated();
		if (game.getName() == null) {
			game.setName("");
		}
		gameDal.persist(game);
		getGameCache(game.getId());
		if (game.getName().isEmpty()) {
			game.setName(GameNameFile.getName(game.getId()));
		}
	}

	@Override
	public void persist(Relation relation) {
		gameDal.persist(relation);
		getGameCache(relation.getGame()).add(relation);
	}

	@Override
	public void persist(RelationChangeAudit relationChangeAudit) {
		gameDal.persist(relationChangeAudit);
	}

	@Override
	public void remove(Game game) {
		gameDal.remove(game);
		dataCache.remove(game);
	}

	@Override
	public void removeGame(int id) {
		remove(getGameCache(id).getGame());
	}

	@Override
	public List<Game> getUnjoinedGames(Player player) {
		List<Game> retval = new ArrayList<Game>();
		for (GameCache gameCache : dataCache.getGameCaches()) {
			if (gameCache.getNation(player) == null) {
				retval.add(gameCache.getGame());
			}
		}
		return retval;
	}

	@Override
	public List<GameBuildAudit> getGameBuildAudit() {
		return gameDal.getGameBuildAudit();
	}

	@Override
	public void remove(Relation relation) {
		gameDal.remove(relation);
	}

	@Override
	public void remove(City city) {
		gameDal.remove(city);
	}

	@Override
	public Collection<Nation> getFriendsAndAllies(Nation nation) {
		Set<RelationType> relations = Sets.immutableEnumSet(RelationType.ALLIED, RelationType.FRIENDLY);
		return getRelations(nation, relations);
	}

	@Override
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
