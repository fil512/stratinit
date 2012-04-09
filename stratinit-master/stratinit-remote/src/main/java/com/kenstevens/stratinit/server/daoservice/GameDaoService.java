package com.kenstevens.stratinit.server.daoservice;

import java.util.Date;
import java.util.List;

import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.GameBuildAudit;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.model.RelationPK;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.RelationType;

public interface GameDaoService {

	Game createGame(String name);

	Game createBlitzGame(String name, int islands);

	void mapGame(Game game);

	void scheduleGame(Game game);

	Result<Nation> joinGame(Player player, int gameId, boolean noAlliances);

	List<Game> getUnjoinedGames(Player player);

	List<Game> getJoinedGames(Player player);

	Game findGame(int gameId);

	void removeGame(int gameId);

	Result<Relation> setRelation(Nation nation, Nation target,
			RelationType newRelation, boolean override);

	void switchRelation(RelationPK relationPK);

	void switchRelation(Relation relation);

	void disable(Game game);

	// TODO record more about the game
	void score(Game game);

	void merge(Nation nation);

	List<GameBuildAudit> getGameBuildAudit();

	void remove(Relation relation);

	void updateGame(Game game, Date lastUpdated);

	List<SITeam> getTeams(Game game);

	void setNoAlliances(Game game);

	void merge(Game game);

	void calculateAllianceVote(Game game);
}