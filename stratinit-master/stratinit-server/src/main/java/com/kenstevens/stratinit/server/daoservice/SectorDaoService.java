package com.kenstevens.stratinit.server.daoservice;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.supply.Supply;
import com.kenstevens.stratinit.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// TODO OPT pull stuff out of daoservices that only hit cache
@Service
public class SectorDaoService {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SectorDao sectorDao;
    @Autowired
    private CityDao cityDao;
    @Autowired
    private UnitDao unitDao;

    @Autowired
    private UnitDaoService unitDaoService;
    @Autowired
    private CityDaoService cityDaoService;
    @Autowired
    private GameDaoService gameDaoService;
    @Autowired
    private LogDaoService logDaoService;
    @Autowired
    private RelationDaoService relationDaoService;

    @Autowired
    private DataCache dataCache;

    // TODO DEPL persistence.xml read-only on getters
    public WorldView getAllWorldView(Nation nation) {
        Game game = gameDaoService.findGame(nation.getGame().getId());
        List<Sector> sectors;
        sectors = dataCache.getSectors(nation.getGameId());
        return getWorldView(nation, sectors, unitDao.getUnits(game));
    }

    public WorldView getSupplyWorldView(Unit unit) {
        return getRadiusWorldView(unit, Constants.SUPPLY_RADIUS);
    }

    public WorldSector getSectorView(Unit unit) {
        return getRadiusWorldView(unit, 1).getWorldSectorOrNull(unit);
    }

    private WorldView getRadiusWorldView(Unit unit, int distance) {
        return getRadiusWorldView(unit.getCoords(), unit.getNation(), distance);
    }

    private WorldView getRadiusWorldView(SectorCoords coords, Nation nation,
                                         int distance) {
        List<Sector> sectors = dataCache.getWorld(nation.getGameId())
                .getSectorsWithin(coords, distance);
        return getWorldView(nation, sectors, unitDaoService.getTeamUnits(
                nation, relationDaoService.getAllies(nation)));
    }

    public WorldView getInterdictionWorldView(Unit unit, Nation nation) {
        // Need to get a big enough world view to see all surrounding units and
        // their supply
        return getRadiusWorldView(unit.getCoords(), nation,
                Constants.SUPPLY_RADIUS + UnitBase.getLargestInterdictsSight());
    }

    public WorldView getInterceptionWorldView(SectorCoords coords, Nation nation) {
        // Need to get a big enough world view to see all surrounding units and
        // their supply
        return getRadiusWorldView(coords, nation, Constants.INTERCEPTION_RADIUS);
    }

    public WorldView getSeenWorldView(Nation nation) {
        Collection<Sector> sectors = sectorDao
                .getNationSectorsSeenSectors(nation);
        Set<Unit> units = unitDaoService.getTeamUnits(nation, relationDaoService
                .getAllies(nation));
        return getWorldView(nation, sectors, units);
    }

    private WorldView getWorldView(Nation nation, Collection<Sector> sectors,
                                   Collection<Unit> units) {
        WorldView worldView = new WorldView(nation, relationDaoService
                .getMyRelationsAsMap(nation), relationDaoService
                .getTheirRelationTypesAsMap(nation));
        setWorldSectorsFromCities(sectors, worldView);
        if (units != null) {
            setWorldSectorsFromUnits(nation, worldView, units);
        }
        return worldView;
    }

    private void setWorldSectorsFromCities(Collection<Sector> sectors,
                                           WorldView worldView) {
        List<WorldSector> friendlyCities = new ArrayList<WorldSector>();
        Map<SectorCoords, City> cityMap = cityDao.getCityMap(worldView
                .getGame());
        for (Sector sector : sectors) {
            WorldSector worldSector = new WorldSector(sector);
            worldView.setWorldSector(worldSector);
            if (!worldSector.isPlayerCity()) {
                continue;
            }
            City city = cityMap.get(sector.getCoords());
            if (city != null) {
                Nation cityNation = city.getNation();
                worldSector.setNation(cityNation);
                setWorldSectorRelations(worldView, worldSector, cityNation);
                worldSector.setCityType(city.getType());
                if (worldSector.onMyTeam()) {
                    friendlyCities.add(worldSector);
                }
            }
        }
        for (WorldSector citySector : friendlyCities) {
            citySector.setSuppliesLand(true);
            if (citySector.isPort()) {
                citySector.setSuppliesNavy(true);
            }
        }
    }

    private void setWorldSectorsFromUnits(Nation nation, WorldView worldView,
                                          Collection<Unit> units) {
        for (Unit unit : units) {
            WorldSector worldSector = worldView.getWorldSectorOrNull(unit.getCoords());
            if (worldSector == null) {
                continue;
            }
            Nation unitNation = unit.getNation();
            if (!worldSector.isPlayerCity()) {
                worldSector.setNation(unitNation);
                setWorldSectorRelations(worldView, worldSector, unitNation);
            }
            worldSector.addFlak(unit.getFlak());
            worldSector.pickTopUnit(unit);

            if (unitNation.equals(nation)) {
                setSupply(unit, worldSector);
            }
            if (worldSector.onMyTeam()) {
                setCarrier(unit, worldSector);
            }
        }
    }

    private void setCarrier(Unit unit, WorldSector worldSector) {
        if (unit.getType().equals(UnitType.CARRIER)) {
            worldSector.setHoldsFriendlyCarrier(true);
        }
        if (unit.isCapital()) {
            worldSector.setHoldsMyCapital(true);
        }
    }

    private void setSupply(Unit unit, WorldSector worldSector) {
        if (worldSector.isWater() && unit.isTransport()) {
            worldSector.setHoldsMyTransport(true);
        }
        if (unit.getAmmo() > 0) {
            worldSector.setSuppliesLand(worldSector.isSuppliesLand()
                    || unit.isSuppliesLand());
            worldSector.setSuppliesNavy(worldSector.isSuppliesNavy()
                    || unit.isSuppliesNavy());
        }
    }


    private void setWorldSectorRelations(WorldView worldView,
                                         WorldSector worldSector, Nation sectorNation) {
        RelationType myRelation = worldView.getMyRelation(sectorNation);
        // Pick the worst relation
        if (worldSector.getMyRelation() == null
                || worldSector.getMyRelation().compareTo(myRelation) > 0) {
            worldSector.setMyRelation(myRelation);
        }
        RelationType theirRelation = worldView.getTheirRelation(sectorNation);
        if (worldSector.getTheirRelation() == null
                || worldSector.getTheirRelation().compareTo(theirRelation) > 0) {
            worldSector.setTheirRelation(theirRelation);
        }
    }

    public int getLandUnitWeight(WorldSector worldSector) {
        Collection<Unit> units = unitDao.getUnits(worldSector);
        int retval = 0;
        for (Unit unit : units) {
            if (!unit.isLand()) {
                continue;
            }
            retval += unit.getWeight();
        }
        return retval;
    }

    public void explodeSupply(Nation nation, SectorCoords coords) {
        WorldView worldView = getAllWorldView(nation);
        Collection<Unit> units = unitDao.getUnitsWithin(worldView, nation,
                coords, Constants.SUPPLY_RADIUS);
        for (Unit unit : units) {
            Supply supply = new Supply(worldView);
            if (supply.inSupply(unit)) {
                supply.resupply(unit);
                unitDao.merge(unit);
            }
        }
    }

    public WorldSector refreshWorldSector(Nation nation, WorldView worldView,
                                          WorldSector targetSector) {
        List<Sector> sectors = new ArrayList<>();
        sectors.add(dataCache.getWorld(nation.getGameId()).getSectorOrNull(
                targetSector.getCoords()));
        setWorldSectorsFromCities(sectors, worldView);
        Collection<Unit> units = unitDao.getUnits(targetSector);
        setWorldSectorsFromUnits(nation, worldView, units);
        return worldView.getWorldSectorOrNull(targetSector);
    }

    public Set<Nation> devastate(Unit attackerUnit, Sector sector, boolean isInitialAttack) {
        Set<Nation> retval = Sets.newHashSet();
        if (sector.getType() == SectorType.PLAYER_CITY) {
            City city = cityDao.getCity(sector);
            if (city == null) {
                throw new IllegalStateException("Cannot find player city at "
                        + sector);
            }
            CityNukedBattleLog cityNukedBattleLog = new CityNukedBattleLog(
                    attackerUnit, city.getNation(), sector.getCoords());
            if (isInitialAttack) {
                relationDaoService.setRelation(city.getNation(), attackerUnit
                        .getNation(), RelationType.WAR, true);
                retval.add(city.getNation());
            }
            logDaoService.save(cityNukedBattleLog);
            cityDaoService.remove(city);
            sector.setType(SectorType.WASTELAND);
            sectorDao.markCacheModified(sector);
        }
        List<Unit> units = Lists.newArrayList(unitDao.getUnits(sector));
        for (Unit unit : units) {
            unitDaoService.killUnit(unit);
            UnitAttackedBattleLog unitAttackedBattleLog = new UnitAttackedBattleLog(
                    AttackType.NUKE, attackerUnit, unit, sector.getCoords());
            unitAttackedBattleLog.setDefenderDied(true);
            logDaoService.save(unitAttackedBattleLog);
        }
        return retval;
    }

    public void saveIfNew(Nation nation, Sector sector) {
        sectorDao.saveIfNew(nation, sector);
    }

    public void merge(Sector sector) {
        sectorDao.markCacheModified(sector);
    }
}
