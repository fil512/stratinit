package com.kenstevens.stratinit.model;

import com.kenstevens.stratinit.dto.SIUnit;

public class UnitView extends Unit {
	private static final long serialVersionUID = 1L;
	private boolean deleted;
	private boolean isNew;
	private boolean moveIncreased = false;
	private boolean inActionQueue = false;
	
	public UnitView(NationView nationView, SIUnit siunit) {
		super(nationView, siunit.type, siunit.coords, siunit.created);
		this.setId(siunit.id);
		this.setMobility(siunit.mobility);
		this.setFuel(siunit.fuel);
		this.setHp(siunit.hp);
		this.setAmmo(siunit.ammo);
		this.setLastUpdated(siunit.lastUpdated);
		this.setAlive(true);
		if (siunit.nextCoords != null) {
			this.setUnitMove(new UnitMove(this, siunit.nextCoords));
		} 
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	public boolean isNew() {
		return isNew;
	}
	public UnitView copyFrom(UnitView unit) {
		this.setCoords(unit.getCoords());
		if (unit.getMobility() < this.getMobility()) {
			moveIncreased = false;
		} else if (unit.getMobility() > this.getMobility()) {
			moveIncreased = true;
		}
		this.setMobility(unit.getMobility());
		this.setLastUpdated(unit.getLastUpdated());
		this.setFuel(unit.getFuel());
		this.setHp(unit.getHp());
		this.setAmmo(unit.getAmmo());
		this.setCreated(unit.getCreated());
		// TODO REF safer to do the same as what we do in cityview
		this.setUnitMove(unit.getUnitMove());
		return this;
	}
	public void setMoveIncreased(boolean moveIncreased) {
		this.moveIncreased = moveIncreased;
	}
	public boolean isMoveIncreased() {
		return moveIncreased;
	}
	public void setInActionQueue(boolean inActionQueue) {
		this.inActionQueue = inActionQueue;
	}
	public boolean isInActionQueue() {
		return inActionQueue;
	}
}
