package com.kenstevens.stratinit.client.server.rest.session;

import com.kenstevens.stratinit.client.model.Nation;

public class StratInitSession {
    private Nation nation;

    public Nation getNation() {
        return nation;
    }

    public void setNation(Nation nation) {
        this.nation = nation;
    }
}
