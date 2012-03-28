package com.kenstevens.stratinit.dal;

import java.util.List;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.GameBuildAudit;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.NationPK;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.model.audit.RelationChangeAudit;

public interface GameDal {
	Game findGame(int gameId);
	List<Nation> getNations(Game game);
	List<Relation> getRelations(Game game);
	void flush(Game game);
	void flushRelations(List<Relation> relations);
	List<GameBuildAudit> getGameBuildAudit();
	void persist(Game game);
	void persist(Nation nation);
	void persist(Relation relation);
	void persist(RelationChangeAudit relationChangeAudit);
	void remove(Game game);
	void remove(Relation relation);
	void remove(City city);
	void removeNation(NationPK nationPK);
	List<Game> getAllGames();
	void flush(Nation nation);
}
