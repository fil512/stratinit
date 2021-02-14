package com.kenstevens.stratinit.dao.impl;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.cache.GameLoader;
import com.kenstevens.stratinit.dao.GameDao;
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
public class GameDaoImpl extends CacheDaoImpl implements GameDao {
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
	@Override
	public Collection<Nation> getAllies(Nation nation) {
		Set<RelationType> relations = Sets.immutableEnumSet(RelationType.ALLIED);
		return getMutualRelations(nation, relations);
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

	@Override
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
			gameRepo.save(game);
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
	public void save(Nation nation) {
		nationRepo.save(nation);
		getGameCache(nation.getGameId()).add(nation);
	}

	@Override
	@Transactional
	public void save(Game game) {
		game.setCreated();
		if (game.getName() == null) {
			game.setName("");
		}
		gameRepo.save(game);
		getGameCache(game.getId());
		if (game.getName().isEmpty()) {
			game.setName(GameNameFile.getName(game.getId()));
		}
	}

	@Override
	public void save(Relation relation) {
		relationRepo.save(relation);
		getGameCache(relation.getGame()).add(relation);
	}

	@Override
	public void save(RelationChangeAudit relationChangeAudit) {
		relationChangeAuditRepo.save(relationChangeAudit);
	}

	@Override
	@Transactional
	public void remove(Game game) {
		nationRepo.deleteByGame(game);
		unitRepo.deleteByGame(game);
		sectorRepo.deleteByGame(game);
		gameRepo.delete(game);
		dataCache.remove(game);
	}

	@Override
	@Transactional
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
		return gameBuildAuditRepo.findAll();
	}

	@Override
	public void remove(Relation relation) {
		relationRepo.delete(relation);
	}

	@Override
	public void remove(City city) {
		cityRepo.delete(city);
	}

	@Override
	public Collection<Nation> getFriendsAndAllies(Nation nation) {
		Set<RelationType> relations = Sets.immutableEnumSet(RelationType.ALLIED, RelationType.FRIENDLY);
		return getMutualRelations(nation, relations);
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
