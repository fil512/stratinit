package com.kenstevens.stratinit.dto;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.Date;


public class SINation implements StratInitDTO {
    private static final long serialVersionUID = 1L;
    public static final double UNKNOWN = -1.0;
    public int nationId;
    public String name;
    public double tech = UNKNOWN;
    public double dailyTechGain = UNKNOWN;
    public double dailyTechBleed = UNKNOWN;
    public int hourlyCPGain = Constants.UNASSIGNED;
    public int cities;
    public Date lastAction;
	public int wins;
	public int played;
	public int power;
	public boolean newMail;
	public boolean newBattle;
	public SectorCoords startCoords;
	public int commandPoints;
	public int gameId;

	public SINation() {}
	
	public SINation(Nation nation) {
		name = nation.getName();
		nationId = nation.getNationId();
		lastAction = nation.getLastAction();
		wins = nation.getPlayer().getWins();
		played = nation.getPlayer().getPlayed();
		gameId = nation.getGameId();
	}

	public void addPrivateData(Nation me, Nation nation) {
		if (me.equals(nation)) {
			startCoords = nation.getStartCoords();
			tech = nation.getTech();
			dailyTechBleed = nation.getDailyTechBleed();
			dailyTechGain = nation.getDailyTechGain();
			hourlyCPGain = nation.getHourlyCPGain();
			newMail = nation.isNewMail();
			newBattle = nation.isNewBattle();
			commandPoints = nation.getCommandPoints();
		}
	 }
	public String getName() {
		return name;
	}
	public int getCities() {
		return cities;
	}
	public int getPower() {
		return power;
	}
}
