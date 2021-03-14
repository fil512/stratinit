package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.event.svc.EventQueue;
import com.kenstevens.stratinit.server.svc.CityBuilderService;
import com.kenstevens.stratinit.server.svc.FogService;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CityDaoService {
    @Autowired
    private DataCache dataCache;

    @Autowired
    private CityDao cityDao;
    @Autowired
    private SectorDao sectorDao;
    @Autowired
    private UnitDao unitDao;

    @Autowired
    private SectorDaoService sectorDaoService;
    @Autowired
    private MessageDaoService messageDaoService;
    @Autowired
    private LogDaoService logDaoService;

    @Autowired
    private CityBuilderService cityBuilderService;
    @Autowired
    private EventQueue eventQueue;
    @Autowired
    private FogService fogService;


    private void clearCityMove(City city) {
        cityDao.clearCityMove(city);
    }


    public Result<City> updateCity(Nation nation, SectorCoords coords,
                                   CityFieldToUpdateEnum field, UnitType build, UnitType nextBuild,
                                   boolean switchOnTechChange, SectorCoords nextCoords) {

        City city = cityDao.getCity(nation, coords);

        if (city == null) {
            return new Result<>("You don't own city at " + coords, false);
        }

        Result<City> retval;
        if (field == CityFieldToUpdateEnum.BUILD) {
            retval = cityBuilderService.updateBuild(nation, city, build);
        } else if (field == CityFieldToUpdateEnum.NEXT_BUILD) {
            retval = cityBuilderService.updateNextBuild(city, nextBuild);
        } else if (field == CityFieldToUpdateEnum.SWITCH_ON_TECH_CHANGE) {
            retval = new Result<>(true);
            retval.addMessage("Setting switch on tech change in "
                    + city.getCoords() + " to " + switchOnTechChange);
            city.setSwitchOnTechChange(switchOnTechChange);
        } else if (field == CityFieldToUpdateEnum.NEXT_COORDS) {
            retval = new Result<>(true);
            if (nextCoords == null) {
                retval.addMessage("Cancelling waypoint of city "
                        + city.getCoords());
                clearCityMove(city);
            } else {
                retval.addMessage("Setting waypoint of city "
                        + city.getCoords() + " to " + nextCoords);
                setCityMove(city, nextCoords);
            }
        } else {
            throw new IllegalStateException(
                    "Unknown value for UpdateCityField: " + field);
        }

        merge(city);
        retval.setValue(city);
        return retval;
    }


    public void merge(City city) {
        cityDao.markCacheModified(city);
    }

    // Returns old owner
    public Nation captureCity(Nation nation, Sector sector) {
        City city;
        Nation opponent = null;
        city = cityDao.getCity(sector);
        if (city == null) {
            city = new City(sector, nation, UnitType.BASE);
            cityDao.save(city);
        } else {
            opponent = city.getNation();
            cityBuilderService.cityCapturedBuildChange(city);
            cityDao.transferCity(city, nation);
        }
        Sector citySector = dataCache.getWorld(nation.getGameId()).getSectorOrNull(
                sector.getCoords());
        citySector.setType(SectorType.PLAYER_CITY);
        sectorDao.markCacheModified(citySector);
        cityChanged(city);
        return opponent;
    }

    public Nation captureCity(Nation nation, SectorCoords city) {
        Sector sector = dataCache.getWorld(nation.getGameId()).getSectorOrNull(city);
        return captureCity(nation, sector);
    }


    public Result<None> cedeCity(City city, Nation nation) {
        Nation oldOwner = city.getNation();
        cityDao.transferCity(city, nation);
        fogService.survey(nation);
        messageDaoService.notify(nation, oldOwner + " " + city.getCoords()
                + " ceded", oldOwner + " gave you a city at "
                + city.getCoords());
        return new Result<>("City ownership transferred from " + oldOwner
                + " to " + city.getNation(), true);
    }

    public void switchCityBuildsFromTechChange(Nation nation, Date buildTime) {
        List<City> cities = cityDao.getCities(nation);
        for (City city : cities) {
            if (city.isSwitchOnTechChange()
                    && cityBuilderService.switchCityProductionIfTechPermits(city, buildTime)) {
                cityDao.markCacheModified(city);
            }
        }
    }

    public Result<None> establishCity(Unit unit) {
        Nation nation = unit.getNation();
        SectorCoords coords = unit.getCoords();
        Sector sector = sectorDao.getWorld(nation.getGame()).getSectorOrNull(coords);
        if (!canEstablishCity(nation, sector)) {
            return new Result<>("You may not establish a city at " + coords + ".  Cities can only be established on land or next to land and may not be adjacent to other player cities.", false);
        }
        City city = new City(sector, nation, UnitType.BASE);
        cityDao.save(city);
        cityChanged(city);
        sector.setType(SectorType.PLAYER_CITY);
        sectorDao.markCacheModified(sector);
        CityCapturedBattleLog battleLog = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit,
                null, unit.getCoords());
        logDaoService.save(battleLog);
        unit.decreaseMobility(Constants.MOB_COST_TO_CREATE_CITY);
        unitDao.merge(unit);
        return new Result<>("City established at " + coords + ".", true);
    }

    private boolean canEstablishCity(Nation nation, Sector sector) {
        return cityDao.canEstablishCity(nation, sector);
    }

    public Result<None> destroyCity(City city) {

        Sector sector = sectorDao.getWorld(city.getParentGame()).getSectorOrNull(city.getCoords());
        sector.setType(SectorType.START_CITY);
        sectorDao.markCacheModified(sector);

        remove(city);

        return new Result<>("City destroyed at " + sector.getCoords() + ".", true);
    }

    public void remove(City city) {
        eventQueue.cancel(city);
        clearCityMove(city);
        cityDao.delete(city);
    }

    public void cityChanged(City city) {
        fogService.surveyFromCity(city);
        sectorDaoService.explodeSupply(city.getNation(), city.getCoords());
    }

    public void setCityMove(City city, SectorCoords targetCoords) {
        CityMove cityMove = new CityMove(city, targetCoords);
        city.setCityMove(cityMove);
        cityDao.save(cityMove);
        merge(city);
    }

    public void buildUnit(CityPK cityPK, Date buildTime) {
        City city = cityDao.findCity(cityPK);
        cityBuilderService.buildUnit(city, buildTime);
    }
}
