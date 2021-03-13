package com.kenstevens.stratinit.server.svc;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.rest.move.MoveSeen;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.CoordMeasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FogService {
    @Autowired
    UnitDaoService unitDaoService;
    // FIXME remove when add moveseen factory
    @Autowired
    SectorDaoService sectorDaoService;
    @Autowired
    private SectorDao sectorDao;
    @Autowired
    private CityDao cityDao;
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private DataCache dataCache;

    public void survey(Nation nation) {
        List<City> cities = cityDao.getCities(nation);
        List<Unit> units = unitDao.getUnits(nation);
        Set<Sector> seen = new HashSet<Sector>();
        seen.addAll(seeFromCities(cities));
        seen.addAll(seeFromUnits(units));
        MoveSeen moveSeen = new MoveSeen(nation, sectorDaoService, unitDaoService);
        // first ignore subs
        addSectorsSeen(null, seen, moveSeen);
        // now see subs
        for (Unit unit : units) {
            if (unit.isCanSeeSubs()) {
                addSectorsSeen(unit, seen, moveSeen);
            }
        }
        moveSeen.persist();
    }

    public void updateSeen(CoordMeasure coordMeasure, List<Unit> units,
                           MoveSeen moveSeen) {
        unitsSee(units, moveSeen);
        unitsSeen(coordMeasure, units, moveSeen, false);
    }

    public void unitSeen(CoordMeasure coordMeasure, Unit unit,
                         MoveSeen moveSeen, boolean attacking) {
        unitsSeen(coordMeasure, makeUnitList(unit), moveSeen, attacking);
    }

    public void updateSeen(CoordMeasure coordMeasure, Unit unit,
                           MoveSeen moveSeen) {
        updateSeen(coordMeasure, makeUnitList(unit), moveSeen);
    }

    private List<Unit> makeUnitList(Unit unit) {
        List<Unit> units = new ArrayList<>();
        units.add(unit);
        return units;
    }

    public void unitsSeen(CoordMeasure coordMeasure, List<Unit> units,
                          MoveSeen moveSeen, boolean attacking) {
        Collection<Nation> nations;
        if (units.isEmpty()) {
            return;
        }
        Unit topUnit = units.get(0);
        Nation movingNation = topUnit.getNation();
        if (topUnit.isSubmarine() && !attacking) {
            nations = unitDao.getOtherNationsThatCanSeeThisSub(coordMeasure,
                    topUnit);
        } else {
            nations = sectorDao.getOtherNationsThatSeeThisSector(movingNation,
                    topUnit.getCoords());
        }
        if (nations.isEmpty()) {
            // TODO OPT move this elsewhere
            nations = unitDao.getOtherNationsThatSeeThisUnit(topUnit);
            for (Nation nation : nations) {
                unitDaoService.disable(nation, units);
            }
        } else {
            if (!attacking) { // might have sunk enemy
                nations = unitDao.getOtherNationsThatCanSeeThisUnit(
                        coordMeasure, topUnit);
            }
            for (Nation nation : nations) {
                if (!nation.equals(movingNation)) {
                    moveSeen.add(nation, units);
                }
            }
        }
    }

    private Set<Sector> seeFromCities(List<City> cities) {
        Set<Sector> cseen = new HashSet<Sector>();
        for (City city : cities) {
            seeFromCity(cseen, city);
        }
        return cseen;
    }

    private Set<Sector> seeFromUnits(List<Unit> units) {
        Set<Sector> unitSectors = new HashSet<Sector>();
        for (Unit unit : units) {
            unitSectors.addAll(getSectorsSeen(unit));
        }
        return unitSectors;
    }

    public List<Sector> getSectorsSeen(Unit unit) {
        return dataCache.getWorld(unit.getGameId()).getSectorsWithin(
                unit.getCoords(), unit.getSight());
    }

    private void unitsSee(List<Unit> units, MoveSeen moveSeen) {
        for (Unit unit : units) {
            addSectorsSeen(unit, getSectorsSeen(unit), moveSeen);
        }
    }

    public void satelliteSees(LaunchedSatellite satellite, MoveSeen moveSeen) {
        addSectorsSeen(null, dataCache.getWorld(satellite.getGameId())
                .getSectorsWithin(satellite.getCoords(),
                        Constants.SATELLITE_SIGHT), moveSeen);
    }

    private void addSectorsSeen(Unit unitSeeing, Collection<Sector> seen,
                                MoveSeen moveSeen) {
        for (Sector sector : seen) {
            addSectorSeen(unitSeeing, sector, moveSeen);
        }
    }

    private void addSectorSeen(Unit unitSeeing, Sector sector, MoveSeen moveSeen) {
        moveSeen.add(unitSeeing, sector);
    }

    public void surveyFromCity(City city) {
        Set<Sector> cseen = new HashSet<>();
        seeFromCity(cseen, city);
        // FIXME factory
        MoveSeen moveSeen = new MoveSeen(city.getNation(), sectorDaoService, unitDaoService);
        addSectorsSeen(null, cseen, moveSeen);
        moveSeen.persist();
    }

    public void seeFromCity(Set<Sector> cseen, City city) {
        int radius = city.getSightRadius();
        cseen.addAll(dataCache.getWorld(city.getGameId()).getSectorsWithin(
                city.getCoords(), radius));
    }


}
