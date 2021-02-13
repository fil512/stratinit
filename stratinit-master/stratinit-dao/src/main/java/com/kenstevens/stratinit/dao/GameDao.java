package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.model.audit.RelationChangeAudit;
import com.kenstevens.stratinit.type.RelationType;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface GameDao {

	void save(Game game);

	void save(Nation nation);

	void persist(Relation relation);

	List<Game> getAllGames();

	List<Nation> getNations(Game game);

	List<Nation> getNations(Player player);

	Game findGame(int gameId);

	Relation findRelation(Nation from, Nation to);

	Relation findRelation(RelationPK relationPK);

	Nation findNation(Game game, Player player);

	Nation findNation(int gameId, Player player);

	void removeGame(int id);

	void remove(Game game);

	List<Nation> getAllNations(int gameId);

	List<Nation> getAllNations();

	Map<Nation, RelationType> getMyRelationsAsMap(Nation nation);

	Map<Nation, RelationType> getTheirRelationTypesAsMap(
			Nation nation);

	Map<Nation, Relation> getTheirRelationsAsMap(Nation nation);

	Collection<Relation> getMyRelations(Nation nation);

	Collection<Relation> getTheirRelations(Nation nation);

	void merge(Game game);

	void merge(Nation nation);

	Nation getNation(int gameId, int nationId);

	void merge(Relation relation);

	Collection<Relation> getAllChangingRelations(Game game);

	Collection<Nation> getAllies(Nation nation);

	List<Relation> getRelations(Game game);

	void flush();

	void persist(RelationChangeAudit relationChangeAudit);

	Relation getReverse(Relation relation);

	List<Game> getUnjoinedGames(Player player);

	List<GameBuildAudit> getGameBuildAudit();

	void remove(Relation relation);

	void remove(City city);

	Collection<Nation> getFriendsAndAllies(Nation nation);

	List<Game> getAllStartedGames();

	Collection<Nation> getMyRelations(Nation nation, RelationType relationType);
}