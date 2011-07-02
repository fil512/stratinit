package com.kenstevens.stratinit.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;


@Entity
public class Nation implements Serializable {
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	private NationPK nationPK;
	// Note this is not the Primary Key.  It's how clients know the nation.
	// Note also that nationId and islandId bear no relation to one another.
	private int nationId = Constants.UNASSIGNED;
	private double tech = 0.0;
	private Date lastAction;
	private double dailyTechBleed = 0.0;
	private double dailyTechGain = 0.0;
	private int hourlyCPGain = 0;
	private boolean newMail = false;
	private boolean newBattle = false;
	@Embedded
	protected SectorCoords startCoords = new SectorCoords(0,0);
	private int commandPoints = Constants.START_COMMAND_POINTS;

	public Nation() {}

	public Nation(Game game, Player player) {
		this.nationPK = new NationPK(game, player);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((nationPK == null) ? 0 : nationPK.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Nation other = (Nation) obj;
		if (nationPK == null) {
			if (other.nationPK != null)
				return false;
		} else if (!nationPK.equals(other.nationPK))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getName();
	}

	public Player getPlayer() {
		return nationPK.getPlayer();
	}

	public Game getGame() {
		return nationPK.getGame();
	}

	public NationPK getNationPK() {
		return nationPK;
	}

	public void setNationId(int island) {
		this.nationId = island;
	}

	public int getNationId() {
		return nationId;
	}

	public void setTech(double tech) {
		this.tech = tech;
		if (tech > Constants.MAX_TECH) {
			this.tech = Constants.MAX_TECH;
		}
	}

	public double getTech() {
		return tech;
	}

	public void setLastAction(Date lastAction) {
		this.lastAction = lastAction;
	}

	public Date getLastAction() {
		return lastAction;
	}

	public void increaseTech(double techIncrease) {
		setTech(tech + techIncrease);
	}

	public String getName() {
		return nationPK.getPlayer().getUsername();
	}

	public int getGameId() {
		return getGame().getId();
	}

	public int getRadarRadius() {
		return Constants.RADAR_BASE + Math.max(1, (int)Math.min(Constants.MAX_TECH, tech) / Constants.RADAR_FACTOR);
	}

	public void setDailyTechBleed(double dailyTechBleed) {
		this.dailyTechBleed = dailyTechBleed;
	}

	public double getDailyTechBleed() {
		return dailyTechBleed;
	}

	public void setDailyTechGain(double dailyTechGain) {
		this.dailyTechGain = dailyTechGain;
	}

	public double getDailyTechGain() {
		return dailyTechGain;
	}

	public void setNewMail(boolean newMail) {
		this.newMail = newMail;
	}

	public boolean isNewMail() {
		return newMail;
	}

	public void setNewBattle(boolean newBattle) {
		this.newBattle = newBattle;
	}

	public boolean isNewBattle() {
		return newBattle;
	}

	public void setStartCoords(SectorCoords startCoords) {
		this.startCoords = startCoords;
	}

	public SectorCoords getStartCoords() {
		return startCoords;
	}

	public void setCommandPoints(int commandPoints) {
		this.commandPoints = commandPoints;
		if (commandPoints > Constants.MAX_COMMAND_POINTS) {
			this.commandPoints = Constants.MAX_COMMAND_POINTS;
		}
	}

	public int getCommandPoints() {
		return commandPoints;
	}

	public void decreaseCommandPoints(int cost) {
		commandPoints -= cost;
		if (commandPoints < 0) {
			commandPoints = 0;
		}
	}

	public void increaseCommandPoints(int cpGain) {
		setCommandPoints(commandPoints + cpGain);
	}

	public void setHourlyCPGain(int hourlyCPGain) {
		this.hourlyCPGain = hourlyCPGain;
	}

	public int getHourlyCPGain() {
		return hourlyCPGain;
	}
}
