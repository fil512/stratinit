package com.kenstevens.stratinit.client.api;

public abstract class Selection {
    private final Source source;

    public Selection(Selection.Source source) {
        this.source = source;
    }

    public Source getSource() {
        return source;
    }

    public enum Source {CANVAS_SELECT, CANVAS_MOVE, UNIT_TAB, BATTLE_TAB, CITY_TAB, PLAYER_TAB, FUTURE_TAB, CANVAS_SELECT_OTHER}
}
