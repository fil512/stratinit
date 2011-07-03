package com.kenstevens.stratinit.server.daoservice;

import java.util.Date;
import java.util.List;

import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.GameBuildAudit;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.model.RelationPK;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.RelationType;

public interface GameDaoService {

	public abstract Game createGame(String name);

	public abstract Game createBlitzGame(String name, int islands);

	public abstract void mapGame(Game game);

	public abstract void scheduleGame(Game game);

	public abstract Result<Nation> joinGame(Player player, int gameId);

	public abstract List<Game> getUnjoinedGames(Player player);

	public abstract List<Game> getJoinedGames(Player player);

	public abstract Game findGame(int gameId);

	public abstract void removeGame(int gameId);

	public abstract Result<Relation> setRelation(Nation nation, Nation target,
			RelationType newRelation, boolean override);

	public abstract void switchRelation(RelationPK relationPK);

	public abstract void switchRelation(Relation relation);

	public abstract void disable(Game game);

	// TODO record more about the game
	public abstract void score(Game game);

	public abstract void merge(Nation nation);

	public abstract List<GameBuildAudit> getGameBuildAudit();

	public abstract void remove(Relation relation);

	public abstract void updateGame(Game game, Date lastUpdated);

	public abstract List<SITeam> getTeams(Game game);
}