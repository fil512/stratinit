package com.kenstevens.stratinit.model;

import com.kenstevens.stratinit.type.Constants;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Date;


@Entity
public class UnitSeen implements EventKeyed, Serializable {
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	private UnitSeenPK unitSeenPK;
	private boolean enabled = true;

	public UnitSeenPK getUnitSeenPK() {
		return unitSeenPK;
	}

	private Date expiry;

	public UnitSeen() {}
	public UnitSeen(Nation nation, Unit unit) {
		this.unitSeenPK = new UnitSeenPK(nation, unit);
		resetExpiry();
	}

	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}
	public Date getExpiry() {
		return expiry;
	}

	public final void resetExpiry() {
		Date now = new Date();
		int visibilitySeconds = Constants.UNIT_VISIBLE_INTERVAL;
		if (unitSeenPK.getUnit().isSubmarine()) {
			visibilitySeconds = Constants.SUB_VISIBLE_INTERVAL;
		}
		expiry = new Date(now.getTime() + visibilitySeconds * 1000);
	}

	public Nation getNation() {
		return unitSeenPK.getNation();
	}

	public Unit getUnit() {
		return unitSeenPK.getUnit();
	}
	@Override
	public Object getKey() {
		return unitSeenPK;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public Game getGame() {
		return getUnit().getParentGame();
	}
	public String toString() {
		return getNation()+" sees "+getUnit()+" #"+getUnit().getId();
	}

	@Override
	public boolean isKeyUnique() {
		return true;
	}
}
