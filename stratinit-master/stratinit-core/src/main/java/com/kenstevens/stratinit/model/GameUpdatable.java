package com.kenstevens.stratinit.model;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class GameUpdatable extends Updatable {
    private static final long serialVersionUID = 1L;

    protected abstract Game getParentGame();

    public boolean isBlitz() {
        return getParentGame().isBlitz();
    }
}
