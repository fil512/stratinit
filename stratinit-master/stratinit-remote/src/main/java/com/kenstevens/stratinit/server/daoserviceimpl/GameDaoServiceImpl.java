package com.kenstevens.stratinit.server.daoserviceimpl;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.model.audit.RelationChangeAudit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.*;
import com.kenstevens.stratinit.server.event.EventQueue;
import com.kenstevens.stratinit.server.event.RelationManager;
import com.kenstevens.stratinit.server.remote.mail.MailService;
import com.kenstevens.stratinit.server.remote.mail.MailTemplateLibrary;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.util.GameScheduleHelper;
import com.kenstevens.stratinit.world.GameSizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameDaoServiceImpl implements GameDaoService {
	@Autowired
	private PlayerDao playerDao;
	@Autowired
	private GameDao gameDao;
	@Autowired
	private SectorDao sectorDao;
	@Autowired
	private UnitDao unitDao;
	@Autowired
	private MessageDaoService messageDaoService;
	@Autowired
	private SectorDaoService sectorDaoService;
	@Autowired
	private UnitDaoService unitDaoService;
	@Autowired
	private EventQueue eventQueue;
	@Autowired
	private RelationManager relationManager;
	@Autowired
	private TeamCalculator teamCalculator;
	@Autowired
	private MailService mailService;
	@Autowired
	private WorldManager worldManager;
	@Autowired
	private GameCreator gameCreator;

	@Override
	public Game createGame(String name) {
		Game game = new Game(name);
		gameDao.save(game);
		return game;
	}

	@Override
	public Game createBlitzGame(String name, int islands) {
		Game game = new Game(name);
		game.setBlitz(true);
		game.setIslands(islands);
		gameDao.save(game);
		GameScheduleHelper.setStartTimeBasedOnNow(game);
		mapGame(game);
		return game;
	}

	@Override
	public void scheduleGame(Game game) {
		if (game.getStartTime() != null) {
			return;
		}
		GameScheduleHelper.setStartTimeBasedOnNow(game);
		gameDao.merge(game);
		gameDao.flush(); // So the timer can read the game
		eventQueue.schedule(game, false);
		Date started = game.getStartTime();
		if (started != null) {
			for (Nation nation : gameDao.getNations(game)) {
				Player player = nation.getPlayer();
				mailService.sendEmail(player, MailTemplateLibrary
						.getGameScheduled(player, game));
			}
		}
	}

	@Override
	public void mapGame(Game game) {
		if (game.getStartTime() == null) {
			throw new IllegalStateException("Game " + game.getGamename()
					+ " can not be mapped.  It has not been started yet.");
		}
		if (game.isMapped() || game.getMapped() != null) {
			throw new IllegalStateException("Game " + game.getGamename()
					+ " is already mapped.  It cannot be mapped again.");
		}
		if (game.isBlitz()) {
			if (game.getIslands() <= 0) {
				throw new IllegalStateException("Blitz game " + game.getGamename()
						+ " may not be mapped.  It has no islands.");
			}
		} else {
			if (game.getPlayers() <= 0) {
				throw new IllegalStateException("Game " + game.getGamename()
						+ " may not be mapped.  It has no players.");
			}
		}
		GameSizer.setIslands(game);
		GameSizer.setSize(game);
		World world = createWorld(game);
		sectorDao.save(world);
		game.setMapped();
		List<Nation> nations = gameDao.getNations(game);
		int island = 0;
		for (Nation nation : nations) {
			worldManager.addPlayerToMap(island, nation);
			++island;
		}
		setNoAlliances(game);
		gameDao.merge(game);
		gameCreator.createGameIfAllMapped();
	}

	@Override
	public Result<Nation> joinGame(Player player, int gameId, boolean noAlliances) {
		Game game = gameDao.findGame(gameId);
		if (game == null) {
			return new Result<Nation>("no game with id [" + gameId + "].",
					false);
		}
		Nation nation = gameDao.findNation(game, player);
		if (nation != null) {
			return new Result<Nation>("already joined [" + gameId + "].", false);
		}
		if (game.isFullyBooked()) {
			return new Result<Nation>("game is full.", false);
		}
		int nationId = game.addPlayer(noAlliances);
		nation = new Nation(game, player);
		nation.setNoAlliances(noAlliances);
		nation.setNationId(nationId);
		gameDao.save(nation);
		calculateAllianceVote(game);
		gameDao.merge(game);
		setRelations(nation);
		if (game.isMapped()) {
			worldManager.addPlayerToMap(nationId, nation);
		} else if (game.getPlayers() >= Constants.getMinPlayersToSchedule()) {
			scheduleGame(game);
		}
		return new Result<>(nation);
	}

	@Override
	public void calculateAllianceVote(Game game) {
		int noAlliancesVote = 0;
		for (Nation nation : gameDao.getNations(game)) {
			if (nation.isNoAlliances()) {
				++noAlliancesVote;
			}
		}
		game.setNoAlliancesVote(noAlliancesVote);
	}

	private void setRelations(Nation me) {
		List<Nation> nations = gameDao.getNations(me.getGame());
		for (Nation nation : nations) {
			Relation relation = new Relation(me, nation);
			if (nation.equals(me)) {
				relation.setType(RelationType.ME);
			}
			gameDao.save(relation);
			if (!nation.equals(me)) {
				relation = new Relation(nation, me);
				gameDao.save(relation);
			}
		}
	}

	private World createWorld(Game game) {
		return worldManager.build(game);
	}

	@Override
	public List<Game> getUnjoinedGames(Player player) {
		return gameDao.getUnjoinedGames(player);
	}

	@Override
	public List<Game> getJoinedGames(Player player) {
		List<Nation> nations = gameDao.getNations(player);
		List<Game> retval = new ArrayList<Game>();
		for (Nation nation : nations) {
			retval.add(nation.getGame());
		}
		return retval;
	}

	@Override
	public Game findGame(int gameId) {
		return gameDao.findGame(gameId);
	}

	@Override
	public void removeGame(int gameId) {
		gameDao.removeGame(gameId);
	}

	@Override
	public Result<Relation> setRelation(Nation nation, Nation target,
			RelationType newRelation, boolean override) {
		if (nation.equals(target)) {
			return new Result<Relation>(
					"You can't change relations with yourself", false);
		}
		Relation relation = gameDao.findRelation(nation, target);
		if (newRelation == relation.getType()) {
			return new Result<Relation>("Nothing to change.", false, relation);
		}
		Result<Relation> result = relationManager.changeRelation(relation,
				newRelation, override);
		if (result.isSuccess()) {
			messageDaoService.notify(target, nation.toString()
					+ " diplomatic update: " + newRelation, nation.toString()
					+ " " + result);
		}

		return result;
	}

	@Override
	public void switchRelation(RelationPK relationPK) {
		Relation relation = gameDao.findRelation(relationPK);
		switchRelation(relation);
	}

	@Override
	public void switchRelation(Relation relationToSwitch) {
		Relation relation = gameDao.findRelation(relationToSwitch
				.getRelationPK());
		Relation reverse = gameDao.getReverse(relation);
		if (relation.getNextType() == null) {
			// Race condition
			return;
		}
		moveRelationForward(relation);
		gameDao.merge(relation);
		// If the reverse relation is better than me, then degrade it down to
		// me.
		RelationType type = relation.getType();
		if (reverse.getType().compareTo(type) > 0) {
			changeRelation(reverse, type);
		}
		
		if (type.compareTo(RelationType.NEUTRAL) < 0) {
			degradeAllyRelations(relation);
		} else if (type == RelationType.ALLIED) {
			// thirdThemWarThirdMeFriendIAllyThemShouldSwitchThirdNeutralToMe
			ifXatWarWithThemAndXFriendlyWithMeDropXToNeutralWithMe(relation);
		}
	}

	private void ifXatWarWithThemAndXFriendlyWithMeDropXToNeutralWithMe(
			Relation relation) {
		Nation me = relation.getFrom();
		Nation them = relation.getTo();
		for (Nation x : gameDao.getNations(relation.getGame())) {
			Relation xToThem = gameDao.findRelation(x, them);
			if (xToThem.getType() != RelationType.WAR) {
				continue;
			}
			// x is at war with them
			Relation xToMe = gameDao.findRelation(x, me);
			if (xToMe.getType().compareTo(RelationType.NEUTRAL) > 0) {
				changeRelation(xToMe, RelationType.NEUTRAL);
			}
		}
	}

	private void changeRelation(Relation relation, RelationType type) {
		relation.setNextType(type);
		moveRelationForward(relation);
		gameDao.merge(relation);
		eventQueue.cancel(relation);
	}
	
	private void degradeAllyRelations(Relation relation) {
			Nation me = relation.getFrom();
			Nation targetNation = relation.getTo();
			for (Nation ally : gameDao.getAllies(targetNation)) {
				Relation allyRelation = gameDao.findRelation(me, ally);
				if (allyRelation.getType().compareTo(RelationType.NEUTRAL) > 0) {
					changeRelation(allyRelation, RelationType.NEUTRAL);
				}
				Relation allyReverse = gameDao.findRelation(ally, me);
				if (allyReverse.getType().compareTo(RelationType.NEUTRAL) > 0) {
					changeRelation(allyReverse, RelationType.NEUTRAL);
				}
			}
		}

	private void moveRelationForward(Relation relation) {
		RelationChangeAudit relationChangeAudit = new RelationChangeAudit(
				relation);
		gameDao.save(relationChangeAudit);
		relation.setType(relation.getNextType());
		relation.setNextType(null);
		relation.setSwitchTime(null);
		if (relation.getType() == RelationType.ALLIED) {
			Relation reverse = gameDao.getReverse(relation);
			if (reverse.getType() == RelationType.ALLIED) {
				messageDaoService.postBulletin(relation.getGame(), relation
						.getFrom()
						+ " and "
						+ relation.getTo()
						+ " have formed an alliance", null);
			}
		}
	}

	private void updateTech(Game game, Date lastUpdated) {
		List<Nation> nations = gameDao.getNations(game);
		double maxTech = getMaxTech(nations);
		for (Nation nation : nations) {
			updateTech(maxTech, nation, lastUpdated);
		}

	}

	private void updateTech(double maxTech, Nation nation, Date lastUpdated) {
		int techCentres = (int) sectorDao.getNumberOfTechCentres(nation);
		if (techCentres >= Constants.TECH_INCREASE_DAILY_BY_NUM_TECH_CENTRES.length) {
			techCentres = Constants.TECH_INCREASE_DAILY_BY_NUM_TECH_CENTRES.length - 1;
		}
		double allyTech = getMaxTech(gameDao.getAllies(nation));
		double nationTech = nation.getTech();
		double otherTechBleed = (maxTech - nationTech)
				/ Constants.OTHER_TECH_BLEED;
		double allyTechBleed = (allyTech - nationTech)
				/ Constants.ALLY_TECH_BLEED;
		double dailyTechBleed = Math.max(otherTechBleed, allyTechBleed);
		nation.setDailyTechBleed(dailyTechBleed);
		double dailyTechGain = Constants.TECH_INCREASE_DAILY_BY_NUM_TECH_CENTRES[techCentres];
		nation.setDailyTechGain(dailyTechGain);
		double totalDailyTechGain = dailyTechBleed + dailyTechGain;
		int secondsPerDay = Constants.SECONDS_PER_DAY;
		double updatesPerDay = (double) secondsPerDay
				/ Constants.TECH_UPDATE_INTERVAL_SECONDS;
		double netTechGain = totalDailyTechGain / updatesPerDay;
		nation.increaseTech(netTechGain);
		double techAfter = nation.getTech();
		gameDao.merge(nation);
		if (crossedThreshold(nationTech, techAfter)) {
			sectorDaoService
					.switchCityBuildsFromTechChange(nation, lastUpdated);
			unitDaoService.updateCarrierRadar(nation);
			sectorDaoService.survey(nation);
		}
	}

	private boolean crossedThreshold(double nationTech, double techAfter) {
		return Math.floor(nationTech) != Math.floor(techAfter);
	}

	private void setGameLastUpdated(Game game, Date lastUpdated) {
		game.setLastUpdated(lastUpdated);
		gameDao.merge(game);

	}

	private void updateCommandPoints(Game game) {
		List<Nation> nations = gameDao.getNations(game);
		for (Nation nation : nations) {
			int cpGain = capitalPointsToCommandPoints(getCapitalPoints(nation));
			nation.increaseCommandPoints(cpGain);
			nation.setHourlyCPGain(cpGain * 3600 / Constants.TECH_UPDATE_INTERVAL_SECONDS);
			gameDao.merge(nation);
		}
	}

	private int capitalPointsToCommandPoints(int capitalPoints) {
		int cpGain = capitalPoints / Constants.COMMAND_POINT_FACTOR;
		if (cpGain < Constants.MIN_COMMAND_POINTS_GAINED_PER_TICK) {
			cpGain = Constants.MIN_COMMAND_POINTS_GAINED_PER_TICK;
		}
		if (cpGain > Constants.MAX_COMMAND_POINTS_GAINED_PER_TICK) {
			cpGain = Constants.MAX_COMMAND_POINTS_GAINED_PER_TICK;
		}
		return cpGain;
	}


	public int getCapitalPoints(Nation nation) {
		int cities = sectorDao.getNumberOfCities(nation);
		int capitalShips = unitDao.getNumberOfCapitalShips(nation);
		int bases = sectorDao.getNumberOfBases(nation);
		return cities + capitalShips + bases * 2;
	}

	private double getMaxTech(Collection<Nation> collection) {
		double retval = 0.0;
		for (Nation nation : collection) {
			if (nation.getTech() > retval) {
				retval = nation.getTech();
			}
		}
		return retval;
	}

	@Override
	public void disable(Game game) {
		eventQueue.cancel(game);
		game.setEnabled(false);
		gameDao.merge(game);
	}

	// TODO record more about the game
	@Override
	public void score(Game game) {
		Map<Nation, Integer> score = new HashMap<Nation, Integer>();
		int topScore = 0;
		List<Nation> nations = gameDao.getNations(game);
		for (Nation nation : nations) {
			if (score.get(nation) != null) {
				continue;
			}
			Collection<Nation> allies = gameDao.getAllies(nation);
			int cities = 0;
			cities += sectorDao.getNumberOfCities(nation);
			for (Nation ally : allies) {
				cities += sectorDao.getNumberOfCities(ally);
			}
			score.put(nation, cities);
			for (Nation ally : allies) {
				score.put(ally, cities);
			}
			if (cities > topScore) {
				topScore = cities;
			}
		}

		for (Nation nation : nations) {
			Player player = playerDao.find(nation.getPlayer().getId());
			player.addPlayed();
			if (score.get(nation) == topScore) {
				player.addWins();
			}
			playerDao.merge(player);
		}
	}

	@Override
	public void merge(Nation nation) {
		gameDao.merge(nation);
	}

	@Override
	public List<GameBuildAudit> getGameBuildAudit() {
		return gameDao.getGameBuildAudit();
	}

	@Override
	public void remove(Relation relation) {
		eventQueue.cancel(relation);
		gameDao.remove(relation);
	}


	@Override
	public void updateGame(Game game, Date lastUpdated) {
		updateCommandPoints(game);
		updateTech(game, lastUpdated);
		setGameLastUpdated(game, lastUpdated);
	}

	@Override
	public List<SITeam> getTeams(Game game) {
		return teamCalculator.getTeams(game);
	}

	@Override
	public void setNoAlliances(Game game) {
		if (game.getNoAlliancesVote() > game.getPlayers() / 2) {
			game.setNoAlliances(true);
		}
		gameDao.merge(game);
	}

	@Override
	public void merge(Game game) {
		gameDao.merge(game);
	}
}
