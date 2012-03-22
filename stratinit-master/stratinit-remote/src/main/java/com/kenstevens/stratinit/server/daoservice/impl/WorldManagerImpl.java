package com.kenstevens.stratinit.server.daoservice.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.World;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.daoservice.WorldManager;
import com.kenstevens.stratinit.server.remote.event.EventQueue;
import com.kenstevens.stratinit.server.remote.mail.MailService;
import com.kenstevens.stratinit.server.remote.mail.MailTemplateLibrary;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.world.WorldCreator;

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
			throw new IllegalStateException("Island "+island+" on game "+game.getName()+" has no start cities.  game.players = "+game.getPlayers()+".  nation = "+nation+"."+"  nation.nationId="+nation.getNationId());
		}
		nation.setStartCoords(islandCities.get(0).getCoords());
		gameDao.merge(nation);
		boolean firstCity = true;
		for (Sector sector : islandCities) {
			City city;
			sector.setType(SectorType.PLAYER_CITY);
			sectorDao.merge(sector);
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
			sectorDao.persist(city);
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
