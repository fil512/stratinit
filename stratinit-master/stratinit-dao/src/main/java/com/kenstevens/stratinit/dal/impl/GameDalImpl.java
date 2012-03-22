package com.kenstevens.stratinit.dal.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dal.GameDal;
import com.kenstevens.stratinit.dal.SectorDal;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.CityPK;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.GameBuildAudit;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.NationPK;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.model.RelationPK;
import com.kenstevens.stratinit.model.audit.RelationChangeAudit;

@SuppressWarnings("unchecked")
@Service
public class GameDalImpl implements GameDal {
	@PersistenceContext
	protected EntityManager entityManager;
	@Autowired
	private UnitDao unitDao;
	@Autowired
	private SectorDal sectorDal;

	@Override
	public void persist(Game game) {
		entityManager.persist(game);
	}

	@Override
	public void persist(Nation nation) {
		entityManager.persist(nation);
	}

	@Override
	public void persist(Relation relation) {
		entityManager.persist(relation);
	}

	@Override
	public List<Game> getAllGames() {
		return entityManager.createQuery("from Game g where g.enabled = true")
				.getResultList();
	}

	@Override
	public List<Nation> getNations(Game game) {
		return entityManager.createQuery(
				"from Nation n where n.nationPK.game = :game").setParameter(
				"game", game).getResultList();
	}

	@Override
	public Game findGame(int gameId) {
		return entityManager.find(Game.class, gameId);
	}

	private void removeGame(int id) {

		Game game = entityManager.find(Game.class, id);
		if (game == null) {
			return;
		}
		List<Nation> nations = getNations(game);
		for (Nation nation : nations) {
			entityManager.remove(nation);
		}
		unitDao.removeUnits(game);
		sectorDal.remove(game);
		entityManager.remove(game);
	}

	@Override
	public void removeNation(NationPK nationPK) {
		Nation nation = entityManager.find(Nation.class, nationPK);
		if (nation != null) {
			entityManager.remove(nation);
		}
	}

	@Override
	public void remove(Game game) {
		if (game.getId() == null) {
			return;
		}
		removeGame(game.getId());
	}

	@Override
	public List<Relation> getRelations(Game game) {
		return entityManager
				.createQuery(
						"from Relation r where r.relationPK.from.nationPK.game = :game")
				.setParameter("game", game).getResultList();
	}

	@Override
	public void remove(Relation relation) {
		if (relation == null || relation.getRelationPK() == null) {
			return;
		}
		removeRelation(relation.getRelationPK());
	}

	private Relation findRelation(RelationPK relationPK) {
		return entityManager.find(Relation.class, relationPK);
	}
	
	private void removeRelation(RelationPK relationPK) {
		Relation relation = findRelation(relationPK);
		if (relation != null) {
			entityManager.remove(relation);
		}
	
	}

	@Override
	public void remove(City city) {
		if (city == null || city.getCityPK() == null) {
			return;
		}
		removeCity(city.getCityPK());
	}

	private void removeCity(CityPK cityPK) {
		City city = entityManager.find(City.class, cityPK);
		if (city != null) {
			entityManager.remove(city);
		}
	}

	@Override
	public void persist(RelationChangeAudit relationChangeAudit) {
		entityManager.persist(relationChangeAudit);
	}

	@Override
	public List<GameBuildAudit> getGameBuildAudit() {
		return entityManager.createQuery("from GameBuildAudit")
		.getResultList();
	}

	@Override
	public void flush(Game game) {
		entityManager.merge(game);
	}

	@Override
	public void flush(Nation nation) {
		entityManager.merge(nation);
	}

	@Override
	public void flushRelations(List<Relation> relations) {
		for (Relation relation : relations) {
			entityManager.merge(relation);
		}
	}
}
