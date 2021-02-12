package com.kenstevens.stratinit.repo.impl;

import com.kenstevens.stratinit.model.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
// FIXME DELETE
public class SectorDalImpl {
	@PersistenceContext
	protected EntityManager entityManager;

	public void persist(Sector sector) {
		entityManager.persist(sector);
	}

	public void persist(City city) {
		entityManager.persist(city);
	}

	public void persist(SectorSeen sectorSeen) {
		SectorSeen foundSectorSeen = entityManager.find(SectorSeen.class, sectorSeen.getSectorSeenPK());
		if (foundSectorSeen == null) {
			entityManager.persist(sectorSeen);
		}
	}

	public void persist(World world) {
		for (Sector sector : world.getSectors()) {
			entityManager.persist(sector);
		}
	}

	public void flushSectorsSeen(Collection<SectorSeen> sectorsSeen) {
		for (SectorSeen sectorSeen : sectorsSeen) {
			entityManager.merge(sectorSeen);
		}
	}

	public void flush(City city) {
		entityManager.merge(city);
	}

	public void flushSectors(List<Sector> sectors) {
		for (Sector sector : sectors) {
			entityManager.merge(sector);
		}
	}

	public void remove(City city) {
		City foundCity = entityManager.find(City.class, city.getCityPK());
		if (foundCity != null) {
			entityManager.remove(foundCity);
		}
	}

	public List<CityMove> findCityMoves(City city) {
		return entityManager
				.createQuery(
						"from CityMove cm where cm.city = :city")
				.setParameter("city", city).getResultList();
	}
	
	public void flush(CityMove cityMove) {
		entityManager.merge(cityMove);
		
	}
	
	// TODO REF the pattern here needs to be turned into an interface and enforced
	public void flushCityMoves(Collection<CityMove> cityMoves) {
		for (CityMove cityMove : cityMoves) {
			entityManager.merge(cityMove);
		}
	}
	
	public List<CityMove> getCityMoves(Game game) {
		return entityManager
				.createQuery(
						"from CityMove cm where cm.city.nation.nationPK.game = :game")
				.setParameter("game", game).getResultList();
	}
	
	public void persist(CityMove cityMove) {
		entityManager.persist(cityMove);
	}
	
	public void remove(CityMove cityMove) {
		if (cityMove == null || cityMove.getCity() == null) {
			return;
		}
		removeCityMove(cityMove.getId());
	}

	private void removeCityMove(int id) {
		CityMove cityMove = entityManager.find(CityMove.class, id);
		if (cityMove != null) {
			entityManager.remove(cityMove);
		}
	}
}
