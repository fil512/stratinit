package com.kenstevens.stratinit.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Updatable implements EventKeyed, Serializable {
	private static final long serialVersionUID = 1L;

	// FIXME this will duplicate lots of code elsewhere that is calling setLastUpdated(new Date()) after construction.
	private Date lastUpdated = new Date();

	public abstract int getUpdatePeriodMilliseconds();

	
	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}


	public abstract boolean isBlitz();
}
