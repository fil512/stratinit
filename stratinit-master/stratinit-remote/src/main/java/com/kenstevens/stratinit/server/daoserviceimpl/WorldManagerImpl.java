package com.kenstevens.stratinit.server.daoserviceimpl;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.daoservice.WorldManager;
import com.kenstevens.stratinit.server.event.EventQueue;
import com.kenstevens.stratinit.server.remote.mail.MailService;
import com.kenstevens.stratinit.server.remote.mail.MailTemplateLibrary;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.world.WorldCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorldManagerImpl implements WorldManager {
	@Autowired
	private SectorDao sectorDao;
	@Autowired
	private UnitDaoService unitDaoService;
	@Autowired
	private EventQueue eventQueue;
	@Autowired
	private SectorDaoService sectorDaoService;
	@Autowired
	private MailService mailService;
	@Autowired
	private GameDao gameDao;

	private final WorldCreator worldCreator = new WorldCreator();

	public World build(Game game) {
		return worldCreator.build(game);
	}

	@Override
	public void addPlayerToMap(int island, Nation nation) {
		Game game = nation.getGame();
		List<Sector> islandCities = Lists.newArrayList(sectorDao.getStartCitiesOnIsland(game,
				island));
		if (islandCities.isEmpty()) {
			throw new IllegalStateException("Island " + island + " on game " + game.getGamename() + " has no start cities.  game.players = " + game.getPlayers() + ".  nation = " + nation + "." + "  nation.nationId=" + nation.getNationId());
		}
		nation.setStartCoords(islandCities.get(0).getCoords());
		gameDao.markCacheModified(nation);
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
			sectorDao.save(city);
			eventQueue.schedule(city);
			firstCity = false;
		}
		sectorDaoService.survey(nation);
		// TODO REF Why does this need to be down here?
		gameDao.merge(game);
		Player player = nation.getPlayer();
		mailService.sendEmail(player, MailTemplateLibrary.getGameMapped(player, game));
	}

}
