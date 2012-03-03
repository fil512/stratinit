package com.kenstevens.stratinit.model;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class GameUpdatable extends Updatable {
	private static final long serialVersionUID = 1L;

	protected abstract Game getGame();
	
	public boolean isBlitz() {
		return getGame().isBlitz();
	}
}
