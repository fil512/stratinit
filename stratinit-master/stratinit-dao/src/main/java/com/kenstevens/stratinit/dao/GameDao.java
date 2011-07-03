package com.kenstevens.stratinit.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.GameBuildAudit;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.model.RelationPK;
import com.kenstevens.stratinit.model.audit.RelationChangeAudit;
import com.kenstevens.stratinit.type.RelationType;

public interface GameDao {

	public abstract void persist(Game game);

	public abstract void persist(Nation nation);

	public abstract void persist(Relation relation);

	public abstract List<Game> getAllGames();

	public abstract List<Nation> getNations(Game game);

	public abstract List<Nation> getNations(Player player);

	public abstract Game findGame(int gameId);

	public abstract Relation findRelation(Nation from, Nation to);

	public abstract Relation findRelation(RelationPK relationPK);

	public abstract Nation findNation(Game game, Player player);

	public abstract Nation findNation(int gameId, Player player);

	public abstract void removeGame(int id);

	public abstract void remove(Game game);

	public abstract List<Nation> getAllNations(int gameId);

	public abstract List<Nation> getAllNations();

	public abstract Map<Nation, RelationType> getMyRelationsAsMap(Nation nation);

	public abstract Map<Nation, RelationType> getTheirRelationTypesAsMap(
			Nation nation);

	public abstract Map<Nation, Relation> getTheirRelationsAsMap(Nation nation);

	public abstract Collection<Relation> getMyRelations(Nation nation);

	public abstract Collection<Relation> getTheirRelations(Nation nation);

	public abstract void merge(Game game);

	public abstract void merge(Nation nation);

	public abstract Nation getNation(int gameId, int nationId);

	public abstract void merge(Relation relation);

	public abstract Collection<Relation> getAllChangingRelations(Game game);

	public abstract Collection<Nation> getAllies(Nation nation);

	public abstract List<Relation> getRelations(Game game);

	public abstract void flush();

	public abstract void persist(RelationChangeAudit relationChangeAudit);

	public abstract Relation getReverse(Relation relation);

	public abstract List<Game> getUnjoinedGames(Player player);

	public abstract List<GameBuildAudit> getGameBuildAudit();

	public abstract void remove(Relation relation);

	public abstract void remove(City city);

	public abstract Collection<Nation> getFriendsAndAllies(Nation nation);
}