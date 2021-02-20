package com.kenstevens.stratinit.dao.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.cache.NationCache;
import com.kenstevens.stratinit.cache.NationCacheToNationFunction;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.impl.predicates.BaseCityPredicate;
import com.kenstevens.stratinit.dao.impl.predicates.OtherNationPredicate;
import com.kenstevens.stratinit.dao.impl.predicates.SeesSectorPredicate;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.repo.CityMoveRepo;
import com.kenstevens.stratinit.repo.CityRepo;
import com.kenstevens.stratinit.repo.SectorRepo;
import com.kenstevens.stratinit.repo.SectorSeenRepo;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Predicates.and;

@Service
public class SectorDaoImpl extends CacheDaoImpl implements SectorDao {
	@Autowired
	private SectorRepo sectorRepo;
	@Autowired
	private SectorSeenRepo sectorSeenRepo;
	@Autowired
	private CityRepo cityRepo;
	@Autowired
	private CityMoveRepo cityMoveRepo;

	@Override
	public City findCity(CityPK cityPK) {
		return getGameCache(cityPK.getGameId()).getCity(cityPK.getCoords());
	}

	@Override
	public SectorSeen findSectorSeen(Nation nation, SectorCoords coords) {
		return getNationCache(nation).getSectorSeen(coords);
	}

	@Override
	public SectorSeen findSectorSeen(Nation nation, Sector sector) {
		return findSectorSeen(nation, sector.getCoords());
	}

	@Override
	public List<City> getCities(Nation nation) {
		return getNationCache(nation).getCities();
	}

	@Override
	public Collection<City> getCities(Game game) {
		return getGameCache(game).getCities();
	}

	@Override
	public City getCity(Sector sector) {
		return getGameCache(sector.getGame()).getCity(sector.getCoords());
	}

	@Override
	public Map<SectorCoords, City> getCityMap(Game game) {
		return getGameCache(game).getCityMap();
	}

	@Override
	public Collection<Sector> getNationSectorsSeenSectors(Nation nation) {
		return getNationCache(nation).getSectorsSeenSectors(
				getGameCache(nation).getWorld());
	}

	@Override
	public int getNumberOfCities(Nation nation) {
		return getCities(nation).size();
	}

	@Override
	public int getNumberOfBases(Nation nation) {
		return Collections2.filter(getCities(nation), new BaseCityPredicate())
				.size();
	}

	// TODO REF use filter
	@Override
	public long getNumberOfTechCentres(Nation nation) {
		List<City> cities = getCities(nation);
		int retval = 0;
		for (City city : cities) {
			if (city.getType() == CityType.TECH) {
				++retval;
				if (city.getBuild() == UnitType.RESEARCH) {
					++retval;
				}
			}
		}
		return retval;
	}

	@Override
	public Collection<Nation> getOtherNationsThatSeeThisSector(Nation nation,
			SectorCoords coords) {

		List<NationCache> nationCaches = getGameCache(nation).getNationCaches();
		return Collections2.transform(Collections2.filter(
				nationCaches,
				and(new OtherNationPredicate(nation), new SeesSectorPredicate(
						coords))), new NationCacheToNationFunction());
	}

	@Override
	public Collection<SectorSeen> getSectorsSeen(Nation nation) {
		return getNationCache(nation).getSectorsSeen();
	}

	@Override
	public List<City> getSeenCities(Nation nation) {
		List<City> retval = new ArrayList<City>();
		NationCache nationCache = getNationCache(nation);
		for (City city : getGameCache(nation).getCities()) {
			if (nationCache.getSectorSeen(city.getCoords()) != null) {
				retval.add(city);
			}
		}
		return retval;
	}

	@Override
	public Collection<Sector> getStartCitiesOnIsland(Game game, final int island) {
		List<Sector> sectors = getGameCache(game).getSectors();
		return Collections2.filter(sectors, new Predicate<Sector>() {
			@Override
			public boolean apply(Sector sector) {
				return sector.getType() == SectorType.START_CITY
						&& sector.getIsland() == island;
			}
		});
	}

	@Override
	public World getWorld(Game game) {
		return getGameCache(game.getId()).getWorld();
	}

	@Override
	public City getCity(Nation nation, SectorCoords coords) {
		return getGameCache(nation.getGame()).getCity(coords);
	}

	@Override
	public void markCacheModified(City city) {
		getNationCache(city.getNation()).setCityCacheModified(true);
	}

	@Override
	public void markCacheModified(SectorSeen sectorSeen) {
		getNationCache(sectorSeen.getNation()).setSectorSeenModified(true);
	}

	@Override
	public void markCacheModified(Sector sector) {
		getGameCache(sector.getGame()).setWorldModified(true);
	}

	@Override
	public void save(City city) {
        cityRepo.save(city);
        getGameCache(city.getParentGame()).add(city);
    }

	@Override
	public void save(SectorSeen sectorSeen) {
		sectorSeenRepo.save(sectorSeen);
		getNationCache(sectorSeen.getNation()).add(sectorSeen);
	}

	@Override
	@Transactional
	public void save(World world) {
		for (Sector sector : world.getSectors()) {
			sectorRepo.save(sector);
		}
		getGameCache(world.getGame()).setWorld(world);
	}

	@Override
	public void delete(City city) {
        cityRepo.delete(city);
        getGameCache(city.getParentGame()).remove(city);
    }

	@Override
	public void saveIfNew(Nation nation, Sector sector) {
		SectorSeen foundSectorSeen = findSectorSeen(nation, sector);
		if (foundSectorSeen == null) {
			SectorSeen sectorSeen = new SectorSeen(nation, sector);
			save(sectorSeen);
		}
	}

	@Override
	public void transferCity(City city, Nation nation) {
		clearCityMove(city);
		markCacheModified(city);
		getGameCache(city.getParentGame()).transferCity(city, nation);
		markCacheModified(city);
    }

	// TODO REF Move this method into a helper, it doesn't belong here
	@Override
	public boolean canEstablishCity(Nation nation, Sector sector) {
		if (!sector.isLand() && !sector.isWater()) {
			return false;
		}
		World world = getWorld(nation.getGame());
		if (isBesideCity(world, nation, sector)) {
			return false;
		}
		return !world.isAtSea(sector);
	}

	private boolean isBesideCity(World world, Nation nation, Sector sector) {
		for (Sector neighbour : world.getNeighbours(sector)) {
			if (neighbour.isPlayerCity()) {
				City city = getCity(neighbour);
				if (city == null) {
					return true;
				}
				if (!city.getNation().equals(nation)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void clearCityMove(City city) {
		List<CityMove> cityMoves = cityMoveRepo.findByCity(city);
		city.setCityMove(null);
		for (CityMove cityMove : cityMoves) {
			delete(cityMove);
		}
	}

	@Override
	public List<CityMove> getCityMoves(Game game) {
		List<CityMove> retval = Lists.newArrayList();
		for (NationCache nationCache : getGameCache(game).getNationCaches()) {
			retval.addAll(nationCache.getCityMoves());
		}
		return retval;
	}

	@Override
	public void markCacheModified(CityMove cityMove) {
		cityMoveRepo.save(cityMove);
	}

	@Override
	public void save(CityMove cityMove) {
		City city = findCity(cityMove.getCity().getCityPK());
		if (city == null) {
			return;
		}
		cityMoveRepo.save(cityMove);
		getNationCache(city.getNation()).add(cityMove);
	}

	@Override
	public void delete(CityMove cityMove) {
		cityMoveRepo.delete(cityMove);
		getNationCache(cityMove.getCity().getNation()).remove(cityMove);
	}

}
