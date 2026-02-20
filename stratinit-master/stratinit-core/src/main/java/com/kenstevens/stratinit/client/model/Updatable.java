package com.kenstevens.stratinit.client.model;

import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public abstract class Updatable implements EventKeyed, Serializable {
	private static final long serialVersionUID = 1L;

	// TODO this duplicates lots of code elsewhere that is calling setLastUpdated(new Date()) after construction.
	private Date lastUpdated = new Date();

	public abstract long getUpdatePeriodMilliseconds();


	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}


	public abstract boolean isBlitz();
}
