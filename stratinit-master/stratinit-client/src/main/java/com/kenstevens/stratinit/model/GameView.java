package com.kenstevens.stratinit.model;

import java.util.Date;

import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.move.WorldView;

// TODO REF make all these final
public class GameView extends Game {
	private static final long serialVersionUID = 1L;
	private WorldView world;
	private Date lastUpdateTime;
	private final BattleLogList battleLogList = new BattleLogList();
	private final CityList cityList = new CityList();
	private final UnitList unitList = new UnitList();
	private final UnitList seenUnitList = new UnitList();
	private final SatelliteList satelliteList = new SatelliteList();
	private final NationList nationList = new NationList();
	private final TeamList teamList = new TeamList();
	private final MessageList inbox = new MessageList();
	private final MessageList messageBoard = new MessageList();
	private final MessageList sentItems = new MessageList();
	private final NewsLogList newsLogList = new NewsLogList();
	private int nationId;

	public GameView(SIGame sigame) {
		this.setId(sigame.id);
		this.setName(sigame.name);
		this.setSize(sigame.size);
		this.setPlayers(sigame.players);
		this.setNoAlliancesVote(sigame.noAlliancesVote);
		this.setNoAlliances(sigame.noAlliances);
		this.setIslands(sigame.islands);
		this.setCreated(sigame.created);
		this.setMapped(sigame.mapped);
		this.setStartTime(sigame.started);
		this.setEnds(sigame.ends);
		this.setBlitz(sigame.blitz);
	}

	public void setWorld(WorldView world) {
		this.world = world;
	}

	public WorldView getWorld() {
		return world;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public BattleLogList getBattleLogList() {
		return battleLogList;
	}

	public CityList getCityList() {
		return cityList;
	}

	public UnitList getUnitList() {
		return unitList;
	}

	public NationList getNationList() {
		return nationList;
	}

	public TeamList getTeamList() {
		return teamList;
	}

	public MessageList getInbox() {
		return inbox;
	}

	public MessageList getMessageBoard() {
		return messageBoard;
	}

	public MessageList getSentItems() {
		return sentItems;
	}

	public UnitList getSeenUnitList() {
		return seenUnitList;
	}

	public void setNationId(int nationId) {
		this.nationId = nationId;
	}

	public int getNationId() {
		return nationId;
	}

	public SatelliteList getSatelliteList() {
		return satelliteList;
	}

	public NewsLogList getNewsLogList() {
		return newsLogList;
	}

	public NationView getAlly() {
		return nationList.getAlly();
	}
}
