package com.kenstevens.stratinit.model;

import java.util.Date;

import org.springframework.stereotype.Repository;

import com.kenstevens.stratinit.main.ClientConstants;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.type.SectorCoords;

@Repository
public class Data {
	private String serverVersion = "unknown";
	// TODO REF make all lists final and remove persistence stuff
	private final GameList gameList = new GameList();
	private final GameList unjoinedGameList = new GameList();
	private int selectedGameId = -1;
	private boolean loaded = false;
	private GameView selectedGame;
	private boolean loggedIn = false;
	private Date lastLoginTime;

	public String getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(String serverVersion) {
		this.serverVersion = serverVersion;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public GameList getGameList() {
		return gameList;
	}

	public int getSelectedGameId() {
		return selectedGameId;
	}

	public void setSelectedGameId(int selectedGameId) {
		this.selectedGameId = selectedGameId;
		this.setSelectedGame(gameList.get(selectedGameId));
		setLoggedIn(true);
	}

	public void clear() {
		gameList.clear();
	}

	public BattleLogList getBattleLogList() {
		if (getSelectedGame() == null) {
			return null;
		}
		return getSelectedGame().getBattleLogList();
	}

	public int getBoardSize() {
		if (getSelectedGame() == null) {
			return ClientConstants.DEFAULT_BOARD_SIZE;
		}
		return getSelectedGame().getWorld().size();
	}

	public CityList getCityList() {
		if (getSelectedGame() == null) {
			return null;
		}
		return getSelectedGame().getCityList();
	}

	public Date getLastUpdatedTime() {
		if (getSelectedGame() == null) {
			return null;
		}
		return getSelectedGame().getLastUpdateTime();
	}

	public Date getLastLoginTime() {
		if (lastLoginTime == null) {
			lastLoginTime = new Date();
		}
		return lastLoginTime;
	}

	public UnitList getUnitList() {
		if (getSelectedGame() == null) {
			return null;
		}
		return getSelectedGame().getUnitList();
	}

	public SatelliteList getSatelliteList() {
		if (getSelectedGame() == null) {
			return null;
		}
		return getSelectedGame().getSatelliteList();
	}

	public UnitList getSeenUnitList() {
		if (getSelectedGame() == null) {
			return null;
		}
		return getSelectedGame().getSeenUnitList();
	}

	public WorldView getWorld() {
		if (getSelectedGame() == null) {
			return null;
		}
		return getSelectedGame().getWorld();
	}

	public void setLastUpdateTime(Date date) {
		if (getSelectedGame() == null) {
			return;
		}
		getSelectedGame().setLastUpdateTime(date);
	}

	public void setNationId(int nationId) {
		if (getSelectedGame() == null) {
			return;
		}
		getSelectedGame().setNationId(nationId);
	}

	public NationList getNationList() {
		if (getSelectedGame() == null) {
			return null;
		}
		return getSelectedGame().getNationList();
	}

	public MessageList getInbox() {
		if (getSelectedGame() == null) {
			return null;
		}
		return getSelectedGame().getInbox();
	}

	public MessageList getMessageBoard() {
		if (getSelectedGame() == null) {
			return null;
		}
		return getSelectedGame().getMessageBoard();
	}

	public MessageList getSentItems() {
		if (getSelectedGame() == null) {
			return null;
		}
		return getSelectedGame().getSentItems();
	}

	public int getNationId() {
		if (getSelectedGame() == null) {
			return -1;
		}
		return getSelectedGame().getNationId();
	}

	public NationView getNation() {
		if (getSelectedGame() == null) {
			return null;
		}
		return getSelectedGame().getNationList().getNation(getNationId());
	}

	public CityView getCity(WorldSector sector) {
		if (sector == null) {
			return null;
		}
		if (!sector.isMyCity()) {
			return null;
		}
		return getCityList().get(sector.getCoords());
	}

	public CityView getCity(SectorCoords sectorCoords) {
		if (sectorCoords == null) {
			return null;
		}
		WorldView world = getWorld();
		if (world == null) {
			return null;
		}
		return getCity(world.getWorldSector(sectorCoords));
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = true;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public int getGameDay() {
		if (getSelectedGame() == null) {
			return 0;
		}
		Date start = getSelectedGame().getStartTime();
		if (start == null) {
			return 0;
		}
		Date now = new Date();
		long durationMillis = now.getTime() - start.getTime();
		return 1 + (int) durationMillis / (1000 * 60 * 60 * 24);
	}

	public NationView getNation(int nationId) {
		return getNationList().getNation(nationId);
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Date getGameEnds() {
		if (getSelectedGame() == null) {
			return null;
		}
		return getSelectedGame().getEnds();
	}

	public void setSelectedGame(GameView selectedGame) {
		this.selectedGame = selectedGame;
	}

	public GameView getSelectedGame() {
		return selectedGame;
	}

	public Date getGameStarts() {
		if (getSelectedGame() == null) {
			return null;
		}
		return getSelectedGame().getStartTime();
	}

	public GameList getUnjoinedGameList() {
		return unjoinedGameList;
	}

	public NewsLogList getNewsLogList() {
		if (getSelectedGame() == null) {
			return null;
		}
		return getSelectedGame().getNewsLogList();
	}

	public TeamList getTeamList() {
		if (getSelectedGame() == null) {
			return null;
		}
		return getSelectedGame().getTeamList();
	}

	public NationView getAlly() {
		if (getSelectedGame() == null) {
			return null;
		}
		return getSelectedGame().getAlly();
	}
}
