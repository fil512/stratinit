package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.cache.NationCache;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.repo.CityMoveRepo;
import com.kenstevens.stratinit.repo.CityRepo;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CityDao extends CacheDao {
    @Autowired
    private CityRepo cityRepo;
    @Autowired
    private CityMoveRepo cityMoveRepo;

    public City findCity(CityPK cityPK) {
        return getGameCache(cityPK.getGameId()).getCity(cityPK.getCoords());
    }

    public List<City> getCities(Nation nation) {
        return getNationCache(nation).getCities();
    }

    public Collection<City> getCities(Game game) {
        return getGameCache(game).getCities();
    }

    public City getCity(Sector sector) {
        return getGameCache(sector.getGame()).getCity(sector.getCoords());
    }

    public Map<SectorCoords, City> getCityMap(Game game) {
        return getGameCache(game).getCityMap();
    }

    public int getNumberOfCities(Nation nation) {
        return getCities(nation).size();
    }

    public int getNumberOfBases(Nation nation) {
        return (int) getCities(nation).stream().filter(City::isBase).count();
    }

    // TODO REF use filter
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

    public Collection<Sector> getStartCitiesOnIsland(Game game, final int island) {
        List<Sector> sectors = getGameCache(game).getSectors();
        return sectors.stream()
                .filter(sector -> sector.getType() == SectorType.START_CITY
                        && sector.getIsland() == island)
                .collect(Collectors.toList());
    }

    public City getCity(Nation nation, SectorCoords coords) {
        return getGameCache(nation.getGame()).getCity(coords);
    }

    public void markCacheModified(City city) {
        getNationCache(city.getNation()).setCityCacheModified(true);
    }

    public void save(City city) {
        cityRepo.save(city);
        getGameCache(city.getParentGame()).add(city);
    }

    public void delete(City city) {
        cityRepo.delete(city);
        getGameCache(city.getParentGame()).remove(city);
    }

    public void transferCity(City city, Nation nation) {
        clearCityMove(city);
        markCacheModified(city);
        getGameCache(city.getParentGame()).transferCity(city, nation);
        markCacheModified(city);
    }

    public void clearCityMove(City city) {
        List<CityMove> cityMoves = cityMoveRepo.findByCity(city);
        city.setCityMove(null);
        for (CityMove cityMove : cityMoves) {
            delete(cityMove);
        }
    }

    public void markCacheModified(CityMove cityMove) {
        cityMoveRepo.save(cityMove);
    }

    public void save(CityMove cityMove) {
        City city = findCity(cityMove.getCity().getCityPK());
        if (city == null) {
            return;
        }
        cityMoveRepo.save(cityMove);
        getNationCache(city.getNation()).add(cityMove);
    }

    public void delete(CityMove cityMove) {
        cityMoveRepo.delete(cityMove);
        getNationCache(cityMove.getCity().getNation()).remove(cityMove);
    }
}
