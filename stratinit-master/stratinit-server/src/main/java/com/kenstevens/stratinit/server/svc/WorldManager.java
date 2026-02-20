package com.kenstevens.stratinit.server.svc;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.NationDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.event.svc.EventQueue;
import com.kenstevens.stratinit.server.rest.mail.MailService;
import com.kenstevens.stratinit.server.rest.mail.MailTemplateLibrary;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.world.WorldCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorldManager {
    private final WorldCreator worldCreator = new WorldCreator();
    @Autowired
    private SectorDao sectorDao;
    @Autowired
    private CityDao cityDao;
    @Autowired
    private UnitDaoService unitDaoService;
    @Autowired
    private EventQueue eventQueue;
    @Autowired
    private MailService mailService;
    @Autowired
    private GameDao gameDao;
    @Autowired
    private NationDao nationDao;
    @Autowired
    private FogService fogService;

    public World build(Game game) {
        return worldCreator.build(game);
    }

    public void addPlayerToMap(int island, Nation nation) {
        Game game = nation.getGame();
        List<Sector> islandCities = new ArrayList<>(cityDao.getStartCitiesOnIsland(game,
                island));
        if (islandCities.isEmpty()) {
            throw new IllegalStateException("Island " + island + " on game " + game.getGamename() + " has no start cities.  game.players = " + game.getPlayers() + ".  nation = " + nation + "." + "  nation.nationId=" + nation.getNationId());
        }
        nation.setStartCoords(islandCities.get(0).getCoords());
        nationDao.markCacheModified(nation);
        boolean firstCity = true;
        for (Sector sector : islandCities) {
            City city;
            sector.setType(SectorType.PLAYER_CITY);
            sectorDao.markCacheModified(sector);
            if (firstCity) {
                city = new City(sector, nation, UnitType.INFANTRY);
                for (int i = 0; i < Constants.START_INFANTRY; ++i) {
                    Unit unit = unitDaoService.buildUnit(nation, sector.getCoords(), UnitType.INFANTRY);
                    unit.addMobility();
                    unit.addMobility();
                }
            } else {
                city = new City(sector, nation, UnitType.ZEPPELIN);
                for (int i = 0; i < Constants.START_ZEPPELINS; ++i) {
                    Unit unit = unitDaoService.buildUnit(nation, sector.getCoords(), UnitType.ZEPPELIN);
                    unit.addMobility();
                    unit.addMobility();
                }
            }
            cityDao.save(city);
            eventQueue.schedule(city);
            firstCity = false;
        }
        fogService.survey(nation);
        // TODO REF Why does this need to be down here?
        gameDao.merge(game);
        Player player = nation.getPlayer();
        mailService.sendEmail(player, MailTemplateLibrary.getGameMapped(player, game));
    }

}