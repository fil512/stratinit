package com.kenstevens.stratinit.server.rest.svc;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.dao.NationDao;
import com.kenstevens.stratinit.dao.RelationDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.service.CityService;
import com.kenstevens.stratinit.server.service.MessageService;
import com.kenstevens.stratinit.server.service.RelationService;
import com.kenstevens.stratinit.server.service.UnitService;
import com.kenstevens.stratinit.server.svc.FogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NationSvc {
    @Autowired
    private DataCache dataCache;
    @Autowired
    private CityDao cityDao;
    @Autowired
    private NationDao nationDao;
    @Autowired
    private RelationDao relationDao;
    @Autowired
    private UnitService unitService;
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private CityService cityService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private RelationService relationService;
    @Autowired
    private FogService fogService;
    @Autowired
    private PlayerWorldViewUpdate playerWorldViewUpdate;

    public List<SINation> getNations(final Nation me, final boolean includePower) {
        List<Nation> nations = dataCache.getGameCache(me.getGameId())
                .getNations();
        Collection<Nation> allies = relationDao.getAllies(me);
        return nationsToSINations(me, includePower, nations, allies);
    }

    private List<SINation> nationsToSINations(final Nation me,
                                              final boolean includePower, List<Nation> nations, final Collection<Nation> allies) {
        return nations.stream()
                .map(nation -> nationToSINation(me, nation, includePower, allies.contains(nation)))
                .collect(Collectors.toList());
    }

    public SINation nationToSINation(Nation me, Nation nation,
                                     boolean includePower, boolean isAlly) {
        SINation sination = new SINation(nation);
        if (includePower) {
            sination.cities = cityDao.getNumberOfCities(nation);
            sination.power = unitService.getPower(nation);
        }
        if (me != null) {
            sination.addPrivateData(me, nation);
        }
        if (isAlly) {
            sination.tech = nation.getTech();
        }
        return sination;
    }

    private List<SIGame> nationsToSIGames(List<Nation> nations) {
        return nations.stream()
                .map(nation -> new SIGame(nation.getGame(), nation.isNoAlliances()))
                .collect(Collectors.toList());
    }

    public List<SIGame> getJoinedGames(Player player) {
        List<Nation> nations = nationDao.getNations(player);
        return nationsToSIGames(nations);
    }

    public SINation getMyNation(Nation nation) {
        return nationToSINation(nation, nation, false, false);
    }

    public List<SINation> getNations(Game game) {
        List<Nation> nations = dataCache.getGameCache(game)
                .getNations();
        return nationsToSINations(null, true, nations, new ArrayList<Nation>());
    }

    public Result<SIUpdate> concede(Nation nation) {
        List<City> cities = new ArrayList<>(cityDao.getCities(nation));
        List<Unit> units = new ArrayList<>(unitDao.getUnits(nation));

        Collection<Nation> allies = relationService.getAllies(nation);
        Nation ally = null;
        int allyCityCount = 0;
        if (!allies.isEmpty()) {
            ally = allies.iterator().next();
            allyCityCount = cityDao.getNumberOfCities(ally);
        }

        Result<None> result = Result.trueInstance();
        if (ally != null && allyCityCount > 0) {
            for (Unit unit : units) {
                result.or(unitService.cedeUnit(unit, ally));
            }
            for (City city : cities) {
                result.or(cityService.cedeCity(city, ally));
            }
            messageService.postBulletin(nation.getGame(), nation + " conceeded.  All cities and units transferred to " + ally + ".", null);
        } else {
            for (Unit unit : units) {
                result.or(unitService.disbandUnit(unit));
            }
            for (City city : cities) {
                result.or(cityService.destroyCity(city));
            }
            messageService.postBulletin(nation.getGame(), nation + " conceeded.  All cities and units destroyed.", null);
        }
        fogService.survey(nation);
        if (ally != null) {
            fogService.survey(ally);
        }
        SIUpdate siupdate = playerWorldViewUpdate.getWorldViewUpdate(nation);
        return new Result<>(result.getMessages(), true, siupdate, result.getBattleLogs(), result.isSuccess());
    }
}
