package com.kenstevens.stratinit.dal.impl;

import com.kenstevens.stratinit.dal.SectorDal;
import com.kenstevens.stratinit.model.*;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
@Service
public class SectorDalImpl implements SectorDal {
	@PersistenceContext
	protected EntityManager entityManager;

	@Override
	public void persist(Sector sector) {
		entityManager.persist(sector);
	}

	@Override
	public void persist(City city) {
		entityManager.persist(city);
	}

	@Override
	public void persist(SectorSeen sectorSeen) {
		SectorSeen foundSectorSeen = entityManager.find(SectorSeen.class, sectorSeen.getSectorSeenPK());
		if (foundSectorSeen == null) {
			entityManager.persist(sectorSeen);
		}
	}

	@Override
	public void persist(World world) {
		for (Sector sector : world.getSectors()) {
			entityManager.persist(sector);
		}
	}

	@Override
	public void flushSectorsSeen(Collection<SectorSeen> sectorsSeen) {
		for (SectorSeen sectorSeen : sectorsSeen) {
			entityManager.merge(sectorSeen);
		}
	}

	@Override
	public void flush(City city) {
		entityManager.merge(city);
	}

	@Override
	public List<SectorSeen> getSectorsSeen(Game game) {
		return entityManager
				.createQuery(
						"from SectorSeen ss WHERE ss.sectorSeenPK.nation.nationPK.game = :game")
				.setParameter("game", game).getResultList();
	}

	@Override
	public List<City> getCities(Game game) {
		return entityManager.createQuery(
				"from City c WHERE c.cityPK.game = :game")
				.setParameter("game", game).getResultList();
	}

	@Override
	public void flushSectors(List<Sector> sectors) {
		for (Sector sector : sectors) {
			entityManager.merge(sector);
		}
	}

	@Override
	public void remove(City city) {
		City foundCity = entityManager.find(City.class, city.getCityPK());
		if (foundCity != null) {
			entityManager.remove(foundCity);
		}
	}

	private List<Sector> getSectors(Game game) {
		return entityManager.createQuery(
				"from Sector s WHERE s.sectorPK.game = :game").setParameter(
				"game", game).getResultList();
	}

	@Override
	public World getWorld(Game game) {
		World world = new World(game, false);
		List<Sector> sectors = getSectors(game);
		for (Sector sector : sectors) {
			world.setSector(sector);
		}
		return world;
	}
	

	@Override
	public List<CityMove> findCityMoves(City city) {
		return entityManager
				.createQuery(
						"from CityMove cm where cm.city = :city")
				.setParameter("city", city).getResultList();
	}
	

	@Override
	public void flush(CityMove cityMove) {
		entityManager.merge(cityMove);
		
	}
	
	// TODO REF the pattern here needs to be turned into an interface and enforced
	@Override
	public void flushCityMoves(Collection<CityMove> cityMoves) {
		for (CityMove cityMove : cityMoves) {
			entityManager.merge(cityMove);
		}
	}
	
	
	@Override
	public List<CityMove> getCityMoves(Game game) {
		return entityManager
				.createQuery(
						"from CityMove cm where cm.city.nation.nationPK.game = :game")
				.setParameter("game", game).getResultList();
	}
	

	@Override
	public void persist(CityMove cityMove) {
		entityManager.persist(cityMove);
	}
	
	
	@Override
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
