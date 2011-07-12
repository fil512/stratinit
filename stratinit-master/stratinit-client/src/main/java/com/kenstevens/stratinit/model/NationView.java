package com.kenstevens.stratinit.model;

import java.util.Date;

import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.type.RelationType;

public class NationView extends Nation {
	private static final long serialVersionUID = 1L;
	private int cities;
	private int played;
	private int wins;
	private int power;
	private Date lastAction;
	private Relation myRelation;
	private Relation theirRelation;

	public NationView(Game game, SINation sination) {
		super(game, new Player(sination.name, sination.nationId));
		this.cities = sination.cities;
		this.power = sination.power;
		this.setNationId(sination.nationId);
		this.startCoords = sination.startCoords;
		this.setTech(sination.tech);
		this.setDailyTechBleed(sination.dailyTechBleed);
		this.setDailyTechGain(sination.dailyTechGain);
		this.played = sination.played;
		this.wins = sination.wins;
		this.lastAction = sination.lastAction;
		this.setNewMail(sination.newMail);
		this.setNewBattle(sination.newBattle);
		this.setCommandPoints(sination.commandPoints);
		this.setHourlyCPGain(sination.hourlyCPGain);
	}

	public NationView copyFrom(NationView nation) {
		this.setCities(nation.cities);
		if (nation.power > 0) {
			this.setPower(nation.power);
		}
		this.setLastAction(nation.lastAction);
		this.setStartCoords(nation.startCoords);
		this.setTech(nation.getTech());
		this.setPlayed(nation.getPlayed());
		this.setWins(nation.getWins());
		this.setNewMail(nation.isNewMail());
		this.setNewBattle(nation.isNewBattle());
		this.setCommandPoints(nation.getCommandPoints());
		this.setHourlyCPGain(nation.getHourlyCPGain());
		return this;
	}

	public void setCities(int cities) {
		this.cities = cities;
	}

	public int getCities() {
		return cities;
	}

	public boolean isLoggedIn() {
		if (getLastAction() == null) {
			return false;
		}
		return new Date().getTime() - getLastAction().getTime() < 5 * 60 * 1000;
	}

	public void setMyRelation(Relation myRelation) {
		this.myRelation = myRelation;
	}

	public Relation getMyRelation() {
		return myRelation;
	}

	public void setTheirRelation(Relation theirRelation) {
		this.theirRelation = theirRelation;
	}

	public Relation getTheirRelation() {
		return theirRelation;
	}

	public String getMyRelationString() {
		return asString(myRelation);
	}
	
	private String asString(Relation relation) {
		if (relation == null) {
			return "";
		} else if (relation.getType() == RelationType.WAR) {
			return "W";
		} else if (relation.getType() == RelationType.NEUTRAL) {
			return "";
		} else if (relation.getType() == RelationType.FRIENDLY) {
			return "F";
		} else if (relation.getType() == RelationType.ALLIED) {
			return "A";
		}
		return "";
	}

	public void setPlayed(int played) {
		this.played = played;
	}

	public int getPlayed() {
		return played;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getWins() {
		return wins;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public int getPower() {
		return power;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + getNationId();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		NationView other = (NationView) obj;
		if (getNationId() != other.getNationId())
			return false;
		return true;
	}

	public String getTheirRelationString() {
		return asString(theirRelation);
	}

}
