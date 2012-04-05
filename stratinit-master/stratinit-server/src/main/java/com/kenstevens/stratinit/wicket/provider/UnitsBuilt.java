package com.kenstevens.stratinit.wicket.provider;

import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.type.UnitType;

public class UnitsBuilt {
	private int gameId;
	private UnitType type;
	private int count;
	public UnitsBuilt(int gameId, UnitType type) {
		this.gameId = gameId;
		this.type = type;
	}
	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	public UnitType getType() {
		return type;
	}
	public void setType(UnitType type) {
		this.type = type;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getLove() {
		UnitBase unitBase = UnitBase.getUnitBase(type);
		int topTech = UnitBase.getMaxTech() + 1;
		int tech = unitBase.getTech();
		int techFactor = topTech / (topTech - tech);
		int cost = unitBase.getProductionTime();
		return count * techFactor * cost;
	}
	public void increment() {
		++count;
	}
	
}
