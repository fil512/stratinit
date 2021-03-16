package com.kenstevens.stratinit.ui.tabs;

import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

import java.util.Date;

public class UnitEvent {
	private final Date time;
	private final SectorCoords sectorCoords;
	private final UnitType unitType;
	private int move = -1;
	private final UnitEvent.Type eventType;

	public enum Type {
		CREATE, MOVE
	}
	public UnitEvent(Date time, SectorCoords sectorCoords, UnitType unitType,
			UnitEvent.Type eventType) {
		this.time = time;
		this.sectorCoords = sectorCoords;
		this.unitType = unitType;
		this.eventType = eventType;
	}
	public SectorCoords getCoords() {
		return sectorCoords;
	}
	public UnitType getUnitType() {
		return unitType;
	}
	public UnitEvent.Type getEventType() {
		return eventType;
	}
	public Date getTime() {
		return time;
	}
	public void setMove(int move) {
		this.move = move;
	}
	public int getMove() {
		return move;
	}
	public String getMoveString() {
		return move == -1 ? "" : ""+move;
	}
}
